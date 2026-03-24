package org.troy.capstone.search_engine.sorting;
import java.util.List;

import org.troy.capstone.annotations.Generated;

import tech.tablesaw.api.Row;

/**
 * Code is sourced from a MindTap exercise from the course, but modified to fit the project.
 * 
 * Class provides QuickSort implementation for sorting tables based on a specified comparator.
 */
public class QuickSort {

    /** Only exists to prevent Jacoco from reporting this class as uncovered. */
    private QuickSort() {}
    
    /**
     * Sorts a list of rows using the QuickSort algorithm based on the provided comparator.
      * @pre comparator is a valid RowComparator that can compare the rows in the list.
      * 
      * @param rows The list of rows to be sorted.
      * @param comparator The RowComparator used to determine the order of the rows.
     */
    public static void quickSort(List<Row> rows, RowComparator comparator) {
        quickSort(rows, 0, rows.size() - 1, comparator);
    }

    /**
     * Sorts a list of rows using the QuickSort algorithm based on the provided comparator and records the time taken for sorting. Only used in testing so it is ignored in code coverage.
      * 
      * @pre comparator is a valid RowComparator that can compare the rows in the list.
      * @pre rows is not null and the rows container the correct column needed to do the sorting.
      * 
      * @post The list of rows is sorted in place based on the order defined by the comparator, and the time taken for sorting is recorded in the provided LongWrapper if it is not null.
      * @param rows The list of rows to be sorted.
      * @param comparator The RowComparator used to determine the order of the rows.
      * @param time An optional LongWrapper to store the time taken to perform the sort. If null, time will not be recorded.
     */
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
     * Helper method for the QuickSort algorithm that recursively sorts the list of rows based on the provided comparator.
      * @pre low and high are valid indices within the bounds of the rows list, and low is less than high.
      * @pre comparator is a valid RowComparator that can compare the rows in the list.
      * 
      * @post The portion of the list of rows between the low and high indices is sorted in place based on the order defined by the comparator.
      * @param rows The list of rows to be sorted.
      * @param low The starting index of the portion of the list to be sorted.
      * @param high The ending index of the portion of the list to be sorted.
      * @param comparator The RowComparator used to determine the order of the rows.
     */
    private static void quickSort(List<Row> rows, int low, int high, RowComparator comparator) {
        if (low < high) {
            int pivot = partition(rows, low, high, comparator);
            quickSort(rows, low, pivot - 1, comparator);
            quickSort(rows, pivot + 1, high, comparator);
        }
    }

    /**
     * Partitions the list of rows based on the provided comparator and returns the index of the pivot element after partitioning.
      * @pre low and high are valid indices within the bounds of the rows list, and low is less than high.
      * @pre comparator is a valid RowComparator that can compare the rows in the list.
      * 
      * @post The portion of the list of rows between the low and high indices is partitioned in place based on the order defined by the comparator, and the index of the pivot element after partitioning is returned.
      * @param rows The list of rows to be partitioned.
      * @param low The starting index of the portion of the list to be partitioned.
      * @param high The ending index of the portion of the list to be partitioned.
      * @param comparator The RowComparator used to determine the order of the rows for partitioning.
      * @return The index of the pivot element after partitioning.
     */
    private static int partition(List<Row> rows, int low, int high, RowComparator comparator) {
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
     * Swaps two elements in the list of rows.
     * @pre i and j are valid indices within the bounds of the rows list.
     * @post The elements at indices i and j in the list of rows are swapped.
     *
     * @param rows The list of rows in which the elements will be swapped.
     * @param i The index of the first element to be swapped.
     * @param j The index of the second element to be swapped.
     */
    private static void swap(List<Row> rows, int i, int j) {
        Row temp = rows.get(i);
        rows.set(i, rows.get(j));
        rows.set(j, temp);
    }

}
