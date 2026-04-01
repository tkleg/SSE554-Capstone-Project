package org.troy.capstone.search_engine.sorting;

import java.util.List;

import org.troy.capstone.annotations.Generated;
import org.troy.capstone.search_engine.sorting.comparator.RowComparator;

import tech.tablesaw.api.Row;

/**
 * This is used to sort a list of Rows based on a custom RowComparator. Source is https://www.geeksforgeeks.org/dsa/insertion-sort-algorithm/ and modified to fit the data structure and comparators used in the project.
 */
public class InsertionSort {
    
    /** Only exists to prevent Jacoco from reporting this class as uncovered. */
    private InsertionSort() {}

    /**
     * Sorts a list of rows using the Insertion Sort algorithm based on the provided comparator. Only used for analysis so it is ignored in code coverage.
     * 
     * @pre comparator is a valid RowComparator that can compare the rows in the list.
     *  The rows in the list have the proper column that the comparator expects to compare.
     * 
     * @post The list of rows is sorted in place based on the order defined by the comparator.
     * 
     * @param rows The list of rows to be sorted.
     * @param comparator The RowComparator used to compare the rows for sorting.
      * @param time An optional LongWrapper to store the time taken to perform the sort. If null, time will not be recorded.
     */
    @Generated
    public static void insertionSort(List<Row> rows, RowComparator comparator, LongWrapper time) {
        long start = 0;
        if (time != null)
            start = System.nanoTime();
        insertionSort(rows, comparator);
        if (time != null)
            time.setValue(System.nanoTime() - start);
    }

    /**
     * Sorts a list of rows using the Insertion Sort algorithm based on the provided comparator.
     * This method does not record the time taken for sorting.
     * 
     * @pre comparator is a valid RowComparator that can compare the rows in the list.
     *  The rows in the list have the proper column that the comparator expects to compare.
     * 
     * @post The list of rows is sorted in place based on the order defined by the comparator.
     * 
     * @param rows The list of rows to be sorted.
     * @param comparator The RowComparator used to compare the rows for sorting.
     */
    public static void insertionSort(List<Row> rows, RowComparator comparator) {
        int n = rows.size();
        for( int i = 1; i < n; i++) {
            Row key = rows.get(i);
            int j = i - 1;
            while(j >= 0 && comparator.compare(rows.get(j), key) > 0) {
                rows.set(j + 1, rows.get(j));
                j--;
            }
            rows.set(j + 1, key);
        }
    }
}
