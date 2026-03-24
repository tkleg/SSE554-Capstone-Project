package org.troy.capstone.search_engine.sorting;

import java.util.List;

import org.troy.capstone.utils.TableUtils;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

/**
 * The Sorter class provides a method to sort a Table based on a given RowComparator. It uses Insertion Sort for small tables (25 rows or fewer) and Quick Sort for larger tables. The sorting is performed on a copy of the original table, ensuring that the original data remains unchanged.
 */
public class Sorter {

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
     * @return A new Table instance containing the sorted rows from the input table.
     */
    public static Table sortTable(Table table, RowComparator comparator){
        System.out.println("Sorting using " + comparator.toString() + " comparator...");
        List<Row> rows = TableUtils.tableToRowList(table);
        if( rows.size() <= 25 ){
            System.out.println("Using Insertion Sort for small table...");
            InsertionSort.insertionSort(rows, comparator);
        } else {
            System.out.println("Using Quick Sort for larger table...");
            QuickSort.quickSort(rows, comparator);
        }
        Table sortedTable = table.emptyCopy();
        rows.forEach(sortedTable::append);
        return sortedTable;
    }

}
