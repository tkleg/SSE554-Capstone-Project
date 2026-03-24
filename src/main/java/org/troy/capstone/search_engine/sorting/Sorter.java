package org.troy.capstone.search_engine.sorting;

import java.util.List;

import org.troy.capstone.utils.TableUtils;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

/**
 * The Sorter class provides a method to sort a Table based on a given RowComparator. It uses Insertion Sort for small tables (25 rows or fewer) and Quick Sort for larger tables. The sorting is performed on a copy of the original table, ensuring that the original data remains unchanged.
 */
public class Sorter {

    /** Only exists to prevent Jacoco from reporting this class as uncovered */
    private Sorter() {}

    /**
     * Sorts the given table using the specified RowComparator. If the table has 25 rows or fewer, it uses Insertion Sort; otherwise, it uses Quick Sort.
     * 
     * @pre table is not null and contains the necessary columns for the comparator to function properly.
     * @pre comparator is a valid RowComparator that can compare the rows in the table.
     * 
     * @post The returned table is a new Table instance that contains the same rows as the input table but sorted according to the order defined by the comparator. The original table remains unchanged.
     * 
     * @param table The Table to be sorted.
     * @param comparator The RowComparator that defines the sorting order.
     * @param time An optional LongWrapper to store the time taken to perform the sort. If null, time will not be recorded.
     * @return A new Table instance containing the sorted rows from the input table.
     */
    public static Table sortTable(Table table, RowComparator comparator, LongWrapper time) {
        System.out.println("Sorting using " + comparator.toString() + " comparator...");
        List<Row> rows = TableUtils.tableToRowList(table);
        long start = 0;
        if( rows.size() <= 25 ){
            System.out.println("Using Insertion Sort for small table...");
            if( time != null )
                start = System.nanoTime();
            InsertionSort.insertionSort(rows, comparator);
            if( time != null )
                time.setValue(System.nanoTime() - start);
        } else {
            System.out.println("Using Quick Sort for larger table...");
            if( time != null )
                start = System.nanoTime();
            QuickSort.quickSort(rows, comparator);
            if( time != null )
                time.setValue(System.nanoTime() - start);
        }
        Table sortedTable = table.emptyCopy();
        rows.forEach(sortedTable::append);
        return sortedTable;
    }

}
