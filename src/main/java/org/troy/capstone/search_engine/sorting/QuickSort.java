package org.troy.capstone.search_engine.sorting;

import java.util.Comparator;
import java.util.List;


import tech.tablesaw.api.Row;

/**
 * Code is sourced from a MindTap exercise from the course, but modified to fit the project.
 * 
 * Class provides QuickSort implementation for sorting tables based on a specified comparator.
 */
public class QuickSort {
    /**
     * Sorts a list of rows using the QuickSort algorithm based on the provided comparator.
      * @pre comparator is a valid Comparator<Row> that can compare the rows in the list.
      * 
      * @param rows The list of rows to be sorted.
      * @param comparator The comparator used to determine the order of the rows.
     */
    public static void quickSort(List<Row> rows, Comparator<Row> comparator) {
        quickSort(rows, 0, rows.size() - 1, comparator);
    }

    public static void quickSort(List<Row> rows, Comparator<Row> comparator, LongWrapper time) {
        long start = 0L;
        if( time != null )
            start = System.nanoTime();
        quickSort(rows, 0, rows.size() - 1, comparator);
        if (time != null)
            time.setValue(System.nanoTime() - start);
    }

    private static void quickSort(List<Row> rows, int low, int high, Comparator<Row> comparator) {
        if (low < high) {
            int pivot = partition(rows, low, high, comparator);
            quickSort(rows, low, pivot - 1, comparator);
            quickSort(rows, pivot + 1, high, comparator);
        }
    }

    private static int partition(List<Row> rows, int low, int high, Comparator<Row> comparator) {
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

    private static void swap(List<Row> rows, int i, int j) {
        Row temp = rows.get(i);
        rows.set(i, rows.get(j));
        rows.set(j, temp);
    }

}
