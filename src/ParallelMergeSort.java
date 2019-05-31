import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * This class that contains parallel version (using executor service) of Merge Sort
 * @author NubilaPardus
 * @name ForkJoinMergesort
 * @class
 */
public class ParallelMergeSort {
	
	public static final int MIN_GRANULARITY = 256;
	private final ExecutorService executor;
	private final int granularity;

	/**
	 * Constructor
	 * @param {ExecutorService} executor an ExecutorService where the sort tasks will be submitted to
	 */
	public ParallelMergeSort(ExecutorService executor){
		this(executor, MIN_GRANULARITY);
	}

	/**
	 * Provides a new thread-safe instance to sort tasks.
	 * @param {ExecutorService} executor an ExecutorService where the sort tasks will be submitted to
	 * @param {int} granularity the smallest array size for the internal sorting algorithm to sort
	 */
	public ParallelMergeSort(final ExecutorService executor, final int granularity){
		if (null == executor){
			throw new IllegalArgumentException("ExecutorService cannot be null!");
		}
		if (granularity < MIN_GRANULARITY){
			throw new IllegalArgumentException("Minimum granularity value is " + MIN_GRANULARITY + ", got " + granularity);
		}
		this.executor = executor;
		this.granularity = granularity;
	}

	
	/**
	 * Perform an in-place merge and sort on a given array of element. 
	 * @param {Array} comparableElementArray The array of element, <T> The generic type of the element
	 * @param {Class<>} elementClass The class of the element
	 * @return {Array} The sorted array
	 */
	public <T extends Comparable> T[] sort(final T[] comparableElementArray, final Class<T> elementClass)
			throws InterruptedException, ExecutionException {
		if (null == comparableElementArray){
			throw new IllegalArgumentException("Array of elements cannot be null!");
		}
		if (comparableElementArray.length < 2){
			return comparableElementArray;
		}
		for (Future<T> f : executor.invokeAll(build(comparableElementArray, elementClass, 0, comparableElementArray.length))){
			f.get();
		}
		return comparableElementArray;
	}

	/**
	 * Splits the array into smaller executable workloads
	 * @param {Array} comparableElementArray The array of element, <T> The generic type of the element
	 * @param {Class<>} elementClass The class of the element
	 * @param {int} startIndex The start index from the array of elements to be sorted, inclusively.
	 * @param {int} endIndex The end index from the array of elements to be sorted, exclusively.
	 * @return {List<Callable<>>} original array with sorted element
	 */
	private <T extends Comparable> List<Callable<T>> build(T[] comparableElementArray, Class<T> elementClass, int startIndex, int endIndex){
		List<Callable<T>> tasks = new ArrayList<>();
		tasks.add(build(comparableElementArray, startIndex, endIndex, tasks, elementClass));
		return tasks;
	}

	/**
	 * Splits the array into MergeSortTas where each of the task is either a SortTask  or a MergeTask. 
	 * The sort tasks sort an exclusive portion on the array of elements, 
	 * while the merge tasks wait for its dependent sort tasks (or merge tasks) to be finished using a latch. 
	 * When finished, the merge tasks perform a merge operation on its dependent task's elements.
	 * @param {Array} comparableElementArray The array of element, <T> The generic type of the element
	 * @param {int} startIndex The start index from the array of elements to be sorted, inclusively.
	 * @param {int} endIndex The end index from the array of elements to be sorted, exclusively.
	 * @param {List<Callable<>>} tasks 
	 * @param {Class<>} elementClass The class of the element
	 * @return {<T>} 
	 */
	private <T extends Comparable> Callable<T> build(
			T[] comparableElementArray,
			int startIndex,
			int endIndex,
			List<Callable<T>> tasks,
			Class<T> elementClass){

		int len = endIndex - startIndex;
		if (len < granularity) {
			return new SortTask<>(comparableElementArray, startIndex, endIndex);
		} else {
			int middle = len / 2;
			CountDownLatch latch = new CountDownLatch(2);
			tasks.add(new ForkTask<>(latch, build(comparableElementArray, startIndex, startIndex + middle, tasks, elementClass)));
			tasks.add(new ForkTask<>(latch, build(comparableElementArray, startIndex + middle, endIndex, tasks, elementClass)));
			return new JoinTask<>(latch, new MergeTask<>(comparableElementArray, startIndex, startIndex + middle, startIndex + middle, endIndex, elementClass));
		}
	}

	/**
	 * The interfaces for the parallel merge & sort tasks
	 * @name MergeSortTask
	 * @extends Callable<>
	 * @class
	 */
	private static abstract class MergeSortTask<T extends Comparable> implements Callable<T> {
		protected final T[] comparableElementArray;
		protected final int startIndex;
		protected final int endIndex;

		protected MergeSortTask(T[] comparableElementArray, int startIndex, int endIndex) {
			this.comparableElementArray = comparableElementArray;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
	}

	/**
	 * Sort tasks sort the desinated portion on the array of elements.
	 * @name SortTask
	 * @extends MergeSortTask<>
	 * @class
	 */
	private static class SortTask<T extends Comparable> extends MergeSortTask<T> {

		protected SortTask(T[] comparableElementArray, int startIndex, int endIndex) {
			super(comparableElementArray, startIndex, endIndex);
		}

		@Override
		public T call() throws Exception {
			Arrays.sort(comparableElementArray, startIndex, endIndex);
			return null;
		}
	}

	/**
	 * A wrapper that forks a given callable task and track its completion before counting down a latch
	 * @name ForkTask
	 * @extends Callable<>
	 * @class
	 */
	private static class ForkTask<T> implements Callable<T> {
		private final CountDownLatch latch;
		private final Callable<T> task;

		protected ForkTask(CountDownLatch latch, Callable<T> task) {
			this.latch = latch;
			this.task = task;
		}

		@Override
		public T call() throws Exception {
			try {
				return task.call();
			} catch (Exception e){
				throw new ExecutionException("Error while trying to execute forked task " + task.getClass().toString(), e);
			} finally {
				latch.countDown();
			}
		}
	}

	/**
	 * A wrapper tasks that waits for the latch to reach zero before running a callable task.
	 * @name JoinTask
	 * @extends Callable<>
	 * @class
	 */
	private static class JoinTask<T> implements Callable<T> {
		private final CountDownLatch latch;
		private final Callable<T> task;

		private JoinTask(CountDownLatch latch, Callable<T> task) {
			this.latch = latch;
			this.task = task;
		}

		@Override
		public T call() throws Exception {
			latch.await();
			return task.call();
		}
	}

	/**
	 * Merge tasks merge the two disjoint but continuous portions of an array of elements.
	 * @name MergeTask
	 * @extends MergeSortTask<>
	 * @class
	 */
	private static class MergeTask<T extends Comparable> extends MergeSortTask<T> {
		private final Class<T> elementClass;
		private final int leftLength;
		private final int rightLength;

		private MergeTask(
				T[] comparableElementArray,
				int leftStartIndex,
				int leftEndIndex,
				int rightStartIndex,
				int rightEndIndex,
				Class<T> elementClass) {
			super(comparableElementArray, leftStartIndex, rightEndIndex);
			this.leftLength = leftEndIndex - leftStartIndex;
			this.rightLength = rightEndIndex - rightStartIndex;
			this.elementClass = elementClass;
		}

		@Override
		public T call() throws Exception {
			MergeSort.merge(
					comparableElementArray,
					startIndex, leftLength,
					startIndex + leftLength, rightLength,
					elementClass
					);
			return null;
		}
	}
}
