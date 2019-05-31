import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * This class that contains parallel version (using a fork/join pool) of Merge Sort
 * @author NubilaPardus
 * @name ForkJoinMergesort
 * @class
 */
public class ForkJoinMergeSort {
	
	private static final int MIN_GRANULARITY = 256;
	private final int granularity;
	private final ForkJoinPool pool;

	/**
	 * Constructor
	 * @param {ForkJoinPool} pool The ForkJoinPool is a special thread pool which is designed to manage threads
	 * 								and provides us to get information about the thread pool state and performance.
	 */
	public ForkJoinMergeSort(final ForkJoinPool pool){
		this(pool, MIN_GRANULARITY);
	}

	/**
	 * Constructor
	 * @param {ForkJoinPool} pool The ForkJoinPool is a special thread pool which is designed to manage threads
	 * 								and provides us to get information about the thread pool state and performance.
	 * @param {int} granularity the smallest array size for the internal sorting algorithm to sort
	 */
	public ForkJoinMergeSort(final ForkJoinPool pool, final int granularity){
		this.pool = pool;
		this.granularity = granularity;
	}

	
	/**
	 * Perform an in-place merge and sort on a given array of element. 
	 * @param {Array} comparableElementArray The array of element, <T> The generic type of the element
	 * @param {Class<>} elementClass The class of the element
	 * @return {Array} The sorted array
	 */
	public <T extends Comparable> T[] sort(final T[] comparableElementArray, final Class<T> elementClass){
		pool.invoke(new MergeSortTask<>(comparableElementArray, 0, comparableElementArray.length, granularity, elementClass));

		return comparableElementArray;
	}

	/**
	 * The interfaces for the parallel merge & sort tasks
	 * @name MergeSortTask
	 * @extends RecursiveAction
	 * @class
	 */
	private static class MergeSortTask<T extends Comparable> extends RecursiveAction {
		private final T[] comparableElementArray;
		private final int startIndex;
		private final int endIndex;
		private final int granularity;
		private final Class<T> elementClass;

		public MergeSortTask(final T[] comparableElementArray, final int startIndex, final int endIndex, final int granularity, final Class<T> elementClass){
			this.comparableElementArray = comparableElementArray;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.granularity = granularity;
			this.elementClass = elementClass;
		}

		@Override
		protected void compute() {
			int length = endIndex - startIndex;
			int middleIndex = startIndex + length / 2;
			if (length < granularity) {
				Arrays.sort(comparableElementArray, startIndex, endIndex);
			} else {
				invokeAll(
						new MergeSortTask<>(comparableElementArray, startIndex, middleIndex, granularity, elementClass),
						new MergeSortTask<>(comparableElementArray, middleIndex, endIndex, granularity, elementClass)
						);
			}
			MergeSort.merge(comparableElementArray, startIndex, middleIndex - startIndex, middleIndex, endIndex - middleIndex, elementClass);
		}
	}
}
