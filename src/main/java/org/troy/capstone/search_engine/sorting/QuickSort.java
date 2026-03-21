package org.troy.capstone.search_engine.sorting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

/**
 * Code is sourced from a MindTap exercise from the course, but modified to fit the project.
 * 
 * Class provides QuickSort implementation for sorting tables based on a specified comparator.
 */
public class QuickSort {
    public static Table quickSort(Table table, Comparator<Row> comparator) {
        List<Row> rows = new ArrayList<>(table.stream().toList());

        quickSortHelper(rows, 0, table.rowCount() - 1, comparator);
        
        Table newTable = table.emptyCopy();
        rows.stream().forEach(newTable::append);

        return newTable;
    }

    private static void quickSortHelper(List<Row> rows, int low, int high, Comparator<Row> comparator) {
        if (low < high) {
            int pivot = partition(rows, low, high, comparator);
            quickSortHelper(rows, low, pivot - 1, comparator);
            quickSortHelper(rows, pivot + 1, high, comparator);
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
