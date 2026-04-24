package org.troy.capstone.search_engine.sorting;

import java.util.List;

import org.troy.capstone.annotations.Generated;
import org.troy.capstone.search_engine.sorting.comparator.RowComparator;

import tech.tablesaw.api.Row;

/**
 * This is used to sort a list of {@code Row}s based on a custom {@code RowComparator}. Source is https://www.geeksforgeeks.org/dsa/insertion-sort-algorithm/ and modified to fit the data structure and comparators used in the project.
 */
public class InsertionSort {
    
    /** Only exists to prevent Jacoco from reporting this class as uncovered. */
    private InsertionSort() {}

    /**
     * Sorts a list of {@code Row}s using the Insertion Sort algorithm based on the provided {@code RowComparator}. Only used for analysis so it is ignored in code coverage.
     * 
     * @pre comparator is a valid {@code RowComparator} that can compare the {@code Row}s in the list.
     *  The {@code Row}s in the list have the proper column that the comparator expects to compare.
     * 
     * @post The list of {@code Row}s is sorted in place based on the order defined by the comparator.
     * 
     * @param rows The list of {@code Row}s to be sorted.
     * @param comparator The {@code RowComparator} used to compare the {@code Row}s for sorting.
      * @param time An optional {@code LongWrapper} to store the time taken to perform the sort. If null, time will not be recorded.
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
     * Sorts a list of {@code Row}s using the Insertion Sort algorithm based on the provided {@code RowComparator}.
     * This method does not record the time taken for sorting.
     * 
     * @pre comparator is a valid {@code RowComparator} that can compare the {@code Row}s in the list.
     *  The {@code Row}s in the list have the proper column that the comparator expects to compare.
     * 
     * @post The list of {@code Row}s is sorted in place based on the order defined by the comparator.
     * 
     * @param rows The list of {@code Row}s to be sorted.
     * @param comparator The {@code RowComparator} used to compare the {@code Row}s for sorting.
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
