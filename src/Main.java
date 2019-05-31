import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * This class that contains the main method for this project
 * 
 * @author NubilaPardus
 * @name Main
 * @class
 */
public class Main {

	/**
	 * The main method
	 */
	public static void main(String[] args) {
		Integer initialIntegerArray[] = randomArray(2000);
		//System.out.println("Initial Integer Array (lenght: "+ initialIntegerArray.length +"): " + Arrays.toString(initialIntegerArray));
		sequentialMergeSortIntegerTest(initialIntegerArray);
		forkJoinMergeSortIntegerTest(initialIntegerArray);
		executorsMergeSortIntegerTest(initialIntegerArray);

		System.exit(0);
	} 

	/**
	 * Provides random integer Array according to size variable
	 * @param {int} size
	 * @returns {Integer[]}
	 */
	private static Integer[] randomArray(int size) {
		Integer[] retVal = new Integer[size];
		for (int i = 0; i < size; i++){
			retVal[i] = (int)(Math.random() * Integer.MAX_VALUE);
		}
		return retVal;
	}

	/**
	 * Run method of sequential merge sort and print memory usage and total time 
	 * @param {Integer[]} initialArray
	 */
	public static void sequentialMergeSortIntegerTest(Integer[] initialArray) {
		long startTime = System.nanoTime();
		Runtime runtime = Runtime.getRuntime();

		Integer sortedArray[] = MergeSort.sort(initialArray, Integer.class);

		System.out.println("\n -- Sequential version of Merge Sort for Integer Array");
		//System.out.println("Final State: " + Arrays.toString(sortedArray));
		System.out.println("Memory Usage: " + ((runtime.totalMemory() - runtime.freeMemory())/1024) +"kb"); 
		System.out.println("Total Time: "+ (System.nanoTime() - startTime) + "ns");
	}

	/**
	 * Run method of parallel merge sort with using fork/join pool and print memory usage and total time 
	 * @param {Integer[]} initialArray
	 */
	public static void forkJoinMergeSortIntegerTest (Integer[] initialArray) {
		long startTime = System.nanoTime();
		Runtime runtime = Runtime.getRuntime();

		ForkJoinMergeSort sorter = new ForkJoinMergeSort(new ForkJoinPool(runtime.availableProcessors()));
		Integer sortedArray[] = sorter.sort(initialArray, Integer.class);

		System.out.println("\n -- Parallel version (using a fork/join pool) of Merge Sort for Integer Array");
		//System.out.println("Final State: " + Arrays.toString(sortedArray));
		System.out.println("Memory Usage: " + ((runtime.totalMemory() - runtime.freeMemory())/1024) +"kb"); 
		System.out.println("Total Time: "+ (System.nanoTime() - startTime) + "ns");
	}

	/**
	 * Run method of parallel merge sort with using executer service and print memory usage and total time 
	 * @param {Integer[]} initialArray
	 */
	public static void executorsMergeSortIntegerTest(Integer[] initialArray) {
		long startTime = System.nanoTime();
		Runtime runtime = Runtime.getRuntime();

		ParallelMergeSort sorter = new ParallelMergeSort(Executors.newFixedThreadPool(runtime.availableProcessors()));
		Integer sortedArray[] = null;
		try {
			sortedArray = sorter.sort(initialArray, Integer.class);
			
			System.out.println("\n -- Parallel version (using executor service) of Merge Sort for Integer Array");
			//System.out.println("Final State: " + Arrays.toString(sortedArray));
			System.out.println("Memory Usage: " + ((runtime.totalMemory() - runtime.freeMemory())/1024) +"kb"); 
			System.out.println("Total Time: "+ (System.nanoTime() - startTime) + "ns");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}