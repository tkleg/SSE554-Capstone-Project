package org.troy.capstone.search_engine.sorting;

import java.util.Comparator;
import java.util.List;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class Sorter {

    public static boolean isSorted(Table table, Comparator<Row> comparator) {
        for (int i = 1; i < table.rowCount(); i++) {
            if (comparator.compare(table.row(i - 1), table.row(i)) > 0) {
                return false;
            }
        }
        return true;
    }

    public static void testSorts(Table table) {
        List<Comparator<Row>> comparators = Comparators.getComparators();
        for (Comparator<Row> comparator : comparators) {
            LongWrapper time = new LongWrapper();
            Table sortedTable = QuickSort.quickSort(table, comparator, time);
            if (!isSorted(sortedTable, comparator))
                System.out.println("Sorting failed for comparator: " + Comparators.getComparatorName(comparator)+ " in " + time + " seconds");
            else
                System.out.println("Sorting succeeded for comparator: " + Comparators.getComparatorName(comparator)+ " in " + time + " seconds");
        }
    }
}
