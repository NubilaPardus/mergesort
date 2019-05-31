import java.lang.reflect.Array;

/** 
 *  This class that contains sequential version of Merge Sort.
 *  It simply divides the array to be sorted continuously by two elements until they reach the
 *  remaining pieces. Then they merge these pieces into themselves. In this respect, it’s
 *  based on the "divide and conquer" paradigm.
 * 
 * @author NubilaPardus
 * @name MergeSort
 * @class
 */
public class MergeSort {

	/**
	 * Perform an in-place merge and sort on a given array of element. 
	 * @param {Array} comparableElementArray The array of element, <T> The generic type of the element
	 * @param {Class<>} elementClass The class of the element
	 * @return {Array} The sorted array
	 */
	public static <T extends Comparable> T[] sort(T[] comparableElementArray, Class<T> elementClass) {
		if (null == comparableElementArray){
			throw new IllegalArgumentException("array of elements cannot be null");
		}
		if (comparableElementArray.length > 1){
			int middle = (int)Math.floor(comparableElementArray.length / 2);
			sort(comparableElementArray, 0, middle, middle, comparableElementArray.length - middle, elementClass);
		}
		return comparableElementArray;
	}

	/**
	 * Sorts on a given array of element. 
	 * @param {Array} inputArr The array of element, <T> The generic type of the element
	 * @param {int} leftIndex
	 * @param {int} leftLength
	 * @param {int} rightIndex
	 * @param {int} rightLength
	 * @param {Class<>} elementClass The class of the element
	 */
	private static <T extends Comparable> void sort(
			T[] inputArr,
			int leftIndex,
			int leftLength,
			int rightIndex,
			int rightLength,
			Class<T> elementClass) {

		if (leftLength > 2) {
			int middle = (int)Math.floor(leftLength / 2);
			sort(inputArr, leftIndex, middle, leftIndex + middle, leftLength - middle, elementClass);
		} else if (leftLength == 2){
			compareSwap(inputArr, leftIndex, leftIndex + 1);
		}

		if (rightLength > 2){
			int middle = (int)Math.floor(rightLength / 2);
			sort(inputArr, rightIndex, middle, rightIndex + middle, rightLength - middle, elementClass);
		} else if (rightLength == 2){
			compareSwap(inputArr, rightIndex, rightIndex + 1);
		}

		merge(inputArr, leftIndex, leftLength, rightIndex, rightLength, elementClass);
	}

	/**
	 * Merges on a given array of element. 
	 * @param {Array} inputArr The array of element, <T> The generic type of the element
	 * @param {int} leftIndex
	 * @param {int} leftLength
	 * @param {int} rightIndex
	 * @param {int} rightLength
	 * @param {Class<>} elementClass The class of the element
	 */
	@SuppressWarnings("unchecked")
	static <T extends Comparable> void merge(
			T[] inputArr,
			int leftIndex,
			int leftLength,
			int rightIndex,
			int rightLength,
			Class<T> elementClass){
		int smallArrayIndex = leftLength < rightLength ? leftIndex : rightIndex;
		int smallArrayLength = leftLength < rightLength ? leftLength : rightLength;
		T[] tmp = (T[]) Array.newInstance(elementClass, smallArrayLength);
		System.arraycopy(inputArr, smallArrayIndex, tmp, 0, smallArrayLength);

		if (leftLength < rightLength){
			int tmpCur = 0;
			int index = leftIndex;
			int rightCur = rightIndex;

			while (index < rightIndex + rightLength){
				if (tmpCur == leftLength){
					break;
				} else if (rightCur == rightIndex + rightLength){
					inputArr[index++] = tmp[tmpCur++];
				} else if (compare(tmp[tmpCur], inputArr[rightCur]) < 0){
					inputArr[index++] = tmp[tmpCur++];
				} else {
					inputArr[index++] = inputArr[rightCur++];
				}
			}
		} else {
			int tmpCur = rightLength - 1;
			int index = rightIndex + rightLength - 1;
			int leftCur = leftIndex + leftLength - 1;
			while (index >= leftIndex){
				if (tmpCur < 0){
					break;
				} else if (leftCur < leftIndex){
					inputArr[index--] = tmp[tmpCur--];
				} else if (compare(tmp[tmpCur], inputArr[leftCur]) >= 0){
					inputArr[index--] = tmp[tmpCur--];
				} else {
					inputArr[index--] = inputArr[leftCur--];
				}
			}
		}
	}

	/**
	 * First compares and then swaps inside of a given array of element. 
	 * @param {Array} inputArr The array of element, <T> The generic type of the element
	 * @param {int} firstIndex
	 * @param {int} secondIndex
	 */
	private static <T extends Comparable> void compareSwap(T[] inputArr, int firstIndex, int secondIndex){
		if (compare(inputArr, firstIndex, secondIndex) > 0){
			swap(inputArr, firstIndex, secondIndex);
		}
	}
	/**
	 * Compares inside of a given array
	 * @param {Array} inputArr The array of element, <T> The generic type of the element
	 * @param {int} firstIndex
	 * @param {int} secondIndex
	 */
	private static <T extends Comparable> int compare(T[] inputArr, int firstIndex, int secondIndex){
		return compare(inputArr[firstIndex], inputArr[secondIndex]);
	}

	/**
	 * Compares two given elements 
	 * @param {T} first
	 * @param {T} second
	 * @return {int} Returns a negative integer, zero, or a positive integer as this object is lessthan, equal to, or greater than the specified object.
	 */
	@SuppressWarnings("unchecked")
	private static <T extends Comparable> int compare(T first, T second){
		if (null == first || null == second) {
			throw new IllegalArgumentException("Array element cannot be null!");
		}
		return first.compareTo(second);
	}
	
	/**
	 * Swaps inside of a given array
	 * @param {Array} inputArr The array of element, <T> The generic type of the element
	 * @param {int} firstIndex
	 * @param {int} secondIndex
	 */
	private static <T> void swap(T[] inputArr, int firstIndex, int secondIndex){
		T tmp = inputArr[firstIndex];
		inputArr[firstIndex] = inputArr[secondIndex];
		inputArr[secondIndex] = tmp;
	}
}
