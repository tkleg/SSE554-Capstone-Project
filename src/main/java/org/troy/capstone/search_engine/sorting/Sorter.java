package org.troy.capstone.search_engine.sorting;

import java.util.List;

import org.troy.capstone.search_engine.sorting.comparator.RowComparator;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

/**
 * The {@code Sorter} class provides a method to sort a {@code Table} based on a given {@code RowComparator}. It uses Insertion Sort for small tables (25 rows or fewer) and {@code QuickSort} for larger tables. The sorting is performed on a copy of the original table, ensuring that the original data remains unchanged.
 */
public class Sorter {

    /** Threshold for determining when to use {@code Insertion Sort} vs {@code Quick Sort}. If the number of rows is less than or equal to this threshold, {@code Insertion Sort} will be used; otherwise, {@code Quick Sort} will be used. */
    private static final int SORTING_THRESHOLD = 25;
    
    /** Only exists to prevent Jacoco from reporting this class as uncovered and from being instantiated */
    private Sorter() {}
 
    /**
     * Sorts the given table using the specified {@code RowComparator}. If the table has 25 rows or fewer, it uses {@code Insertion Sort}; otherwise, it uses {@code Quick Sort}.
     * 
     * @pre table is not null and contains the necessary columns for the comparator to function properly.
     * @pre comparator
     * is a valid {@code RowComparator} that can compare the rows in the table.
     * 
     * @post The returned table is a new {@code Table} instance that contains the same rows as the input table but sorted according to the order defined by the comparator. The original table remains unchanged.
     * 
     * @param table The {@code Table} to be sorted.
     * @param comparatorObj The {@code RowComparator} that defines the sorting order. This is an Object to make it possible to pass a comparator from another class without the import of the {@code RowComparator} class.
     * @param time An optional {@code LongWrapper} to store the time taken to perform the sort. If null, time will not be recorded.
     * @return A new {@code Table} instance containing the sorted rows from the input table.
     */
    public static Table sortTable(Table table, Object comparatorObj, LongWrapper time) {
        if( comparatorObj == null || !(comparatorObj instanceof RowComparator) ) {
            System.out.println("Invalid comparator provided to Sorter.sortTable. Was provided: " + comparatorObj + ". Returning original table.");
            return table;
        }
        RowComparator comparator = (RowComparator) comparatorObj;
        System.out.println("Sorting using " + comparator.toString() + " comparator...");
        List<Row> rows = TableUtils.tableToRowList(table);

        LongWrapper startTime = new LongWrapper();
        if( time != null )
            startTime = new LongWrapper(System.nanoTime());
        if (rows.size() <= SORTING_THRESHOLD)
            InsertionSort.insertionSort(rows, comparator);
        else
            QuickSort.quickSort(rows, comparator);
        if( time != null )
            time.setValue(System.nanoTime() - startTime.getValue());

        Table sortedTable = table.emptyCopy();
        rows.forEach(sortedTable::append);
        return sortedTable;
    }

    /** Helper method to perform a mixed sort using {@code Insertion Sort} for small lists and {@code Quick Sort} for larger lists, with time measurement. 
     * @pre comparator is a valid {@code RowComparator} that can compare the rows in the list. rows is not null and the rows contain the correct column needed to do the sorting.
     * @post The list of rows is sorted in place based on the order defined by the comparator, and the time taken for sorting is recorded in the provided {@code LongWrapper} if it is not null.
     * @param rows The list of rows to be sorted.
     * @param comparator The {@code RowComparator} used to determine the order of the rows.
     * @param time An optional {@code LongWrapper} to store the time taken to perform the sort. If null, time will not be recorded.
     */
    public static void mixedSort(List<Row> rows, RowComparator comparator, LongWrapper time) {
        mixedSort(rows, 0, rows.size() - 1, comparator, time);
    }

    /**
     * Sorts the rows based on the provided comparator and optionally records the time taken for sorting. Uses {@code Insertion Sort} for small lists and {@code Quick Sort} for larger lists.
      * 
      * @pre comparator is a valid {@code RowComparator} that can compare the rows in the list. rows is not null and the rows contain the correct column needed to do the sorting.
      * @post The list of rows is sorted in place based on the order defined by the comparator, and the time taken for sorting is recorded in the provided {@code LongWrapper} if it is not null.
      * @param rows The list of rows to be sorted.
      * @param comparator The {@code RowComparator} used to determine the order of the rows.
      * @param low The starting index of the portion of the list to be sorted.
      * @param high The ending index of the portion of the list to be sorted.
      * @param time An optional {@code LongWrapper} to store the time taken to perform the sort. If null, time will not be recorded.
     */
    private static void mixedSort(List<Row> rows, int low, int high, RowComparator comparator, LongWrapper time) {
        if( low >= high )
            return;
        long start = 0L;
        if( time != null )
            start = System.nanoTime();
        if (rows.size() <= SORTING_THRESHOLD)
            InsertionSort.insertionSort(rows, comparator);
        else{
            int pivot = QuickSort.partition(rows, low, high, comparator);
            mixedSort(rows, low, pivot - 1, comparator, null);
            mixedSort(rows, pivot + 1, high, comparator, null);
        }
        if (time != null)
            time.setValue(System.nanoTime() - start);
    }

    /** Overloaded method to allow calling sortTable without a {@code LongWrapper} for time measurement. 
     * 
     * @pre table is not null and contains the necessary columns for the comparator to function properly.
     * comparator is a valid {@code RowComparator} that can compare the rows in the table.
     * @post The returned {@code Table} is a new {@code Table} instance that contains the same rows as the input table but sorted according to the order defined by the comparator. The original table remains unchanged.
     * @param table The {@code Table} to be sorted.
     * @param comparator The {@code RowComparator} that defines the sorting order. This is an Object to make it possible to pass a comparator from another class without the import of the {@code RowComparator} class.
     * @return A new {@code Table} instance containing the sorted rows from the input table.
    */
    public static Table sortTable(Table table, Object comparator) {
        return sortTable(table, comparator, null);
    }

}
