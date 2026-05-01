package org.troy.capstone.search_engine.sorting;
import java.util.List;

import org.troy.capstone.annotations.Generated;

import tech.tablesaw.api.Row;

/**
 * Code is sourced from a MindTap exercise from the course, but modified to fit the project.
 * 
 * Class provides {@code QuickSort} implementation for sorting tables based on a specified comparator.
 */
public class QuickSort {

    /** Only exists to prevent Jacoco from reporting this class as uncovered. */
    @Deprecated
    private QuickSort() {}
    
    /**
     * Sorts a list of {@code Row}s using the {@code QuickSort} algorithm based on the provided {@code RowComparator}.
      * @pre comparator is a valid {@code RowComparator} that can compare the {@code Row}s in the list.
      * 
      * @param rows The list of {@code Row}s to be sorted.
      * @param comparator The {@code RowComparator} used to determine the order of the {@code Row}s.
     */
    public static void quickSort(List<Row> rows, RowComparator comparator) {
        quickSort(rows, 0, rows.size() - 1, comparator);
    }

    /**
     * Sorts a list of {@code Row}s using the {@code QuickSort} algorithm based on the provided {@code RowComparator} and records the time taken for sorting. Only used in testing so it is ignored in code coverage.
      * 
      * @pre comparator is a valid {@code RowComparator} that can compare the {@code Row}s in the list.
      * @pre rows is not null and the {@code Row}s contain the correct column needed to do the sorting.
      * 
      * @deprecated This is useful for analyzing the time taken, but it is not part of the main program execution.
      * 
      * @post The list of {@code Row}s is sorted in place based on the order defined by the comparator, and the time taken for sorting is recorded in the provided {@code LongWrapper} if it is not null.
      * @param rows The list of {@code Row}s to be sorted.
      * @param comparator The {@code RowComparator} used to determine the order of the {@code Row}s.
      * @param time An optional {@code LongWrapper} to store the time taken to perform the sort. If null, time will not be recorded.
     */
    @Deprecated
    @Generated
    public static void quickSort(List<Row> rows, RowComparator comparator, LongWrapper time) {
        long start = 0L;
        if( time != null )
            start = System.nanoTime();
        quickSort(rows, 0, rows.size() - 1, comparator);
        if (time != null)
            time.setValue(System.nanoTime() - start);
    }

    /**
     * Helper method for the QuickSort algorithm that recursively sorts the list of {@code Row}s based on the provided {@code RowComparator}.
      * @pre low and high are valid indices within the bounds of the {@code Row}s list, and low is less than high.
      * @pre comparator is a valid {@code RowComparator} that can compare the {@code Row}s in the list.
      * 
      * @post The portion of the list of {@code Row}s between the low and high indices is sorted in place based on the order defined by the comparator.
      * @param rows The list of {@code Row}s to be sorted.
      * @param low The starting index of the portion of the list to be sorted.
      * @param high The ending index of the portion of the list to be sorted.
      * @param comparator The {@code RowComparator} used to determine the order of the {@code Row}s.
     */
    private static void quickSort(List<Row> rows, int low, int high, RowComparator comparator) {
        if (low < high) {
            int pivot = partition(rows, low, high, comparator);
            quickSort(rows, low, pivot - 1, comparator);
            quickSort(rows, pivot + 1, high, comparator);
        }
    }

    /**
     * Partitions the list of {@code Row}s based on the provided {@code RowComparator} and returns the index of the pivot element after partitioning.
      * @pre low and high are valid indices within the bounds of the {@code Row}s list, and low is less than high.
      * @pre comparator is a valid {@code RowComparator} that can compare the {@code Row}s in the list.
      * 
      * @post The portion of the list of {@code Row}s between the low and high indices is partitioned in place based on the order defined by the comparator, and the index of the pivot element after partitioning is returned.
      * @param rows The list of {@code Row}s to be partitioned.
      * @param low The starting index of the portion of the list to be partitioned.
      * @param high The ending index of the portion of the list to be partitioned.
      * @param comparator The {@code RowComparator} used to determine the order of the {@code Row}s for partitioning.
      * @return The index of the pivot element after partitioning.
     */
    static int partition(List<Row> rows, int low, int high, RowComparator comparator) {
        Row pivot = rows.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare(rows.get(j), pivot) <= 0) {
                i++;
                swap(rows, i, j);
            }
        }
        swap(rows, i + 1, high);
        return i + 1;
    }

    /**
     * Swaps two elements in the list of {@code Row}s.
     * @pre i and j are valid indices within the bounds of the {@code Row}s list.
     * @post The elements at indices i and j in the list of {@code Row}s are swapped.
     *
     * @param rows The list of {@code Row}s in which the elements will be swapped.
     * @param i The index of the first element to be swapped.
     * @param j The index of the second element to be swapped.
     */
    private static void swap(List<Row> rows, int i, int j) {
        Row temp = rows.get(i);
        rows.set(i, rows.get(j));
        rows.set(j, temp);
    }

}
