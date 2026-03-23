package org.troy.capstone.search_engine.sorting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

/**
 * Source is https://www.geeksforgeeks.org/dsa/insertion-sort-algorithm/. Modified to fit the data structure and compartors used in the project. 
 */
public class InsertionSort {
    
    public static void insertionSort(List<Row> rows, Comparator<Row> comparator, LongWrapper time) {
        long start = 0;
        if (time != null)
            start = System.nanoTime();
        insertionSort(rows, comparator);
        if (time != null)
            time.setValue(System.nanoTime() - start);
    }

    public static void insertionSort(List<Row> rows, Comparator<Row> comparator) {
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

    public static void main(String[] args) {
        Table table = TableUtils.readCleanedAttributedData();
        LongWrapper time = new LongWrapper();
        List<Row> rows = new ArrayList<>();
        table.stream().forEach(rows::add);
        insertionSort(rows, new RowComparator(RowComparator.SortType.PRICE_ASCENDING), time);
        Table sortedTable = Table.create("Sorted Table");
        rows.forEach(sortedTable::append);
        System.out.println("Time taken to sort: " + time.getValue() + " nanoseconds");
        System.out.println("Is sorted: " + Sorter.isSorted(sortedTable, new RowComparator(RowComparator.SortType.PRICE_ASCENDING)));
    }
}
