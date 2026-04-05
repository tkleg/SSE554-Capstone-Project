package org.troy.capstone.search_engine.sorting;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.TestUtils;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.search_engine.sorting.comparator.RowComparator;
import org.troy.capstone.search_engine.sorting.comparator.RowComparator.SortType;

import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.Table;

public class SorterTest {
    private static final Table testTable = TestDataHolder.getTableCopy().first(50);


    @BeforeAll
    public static void setup() {
        FloatColumn relevanceColumn = FloatColumn.create(TableColumnName.RELEVANCE.getColumnName(), testTable.rowCount());
        for (int i = 0; i < testTable.rowCount(); i++)
            relevanceColumn.set(i, (float) Math.random());
        testTable.addColumns(relevanceColumn);
    }

    @ParameterizedTest
    @EnumSource(RowComparator.SortType.class)
    @DisplayName("Test Sorter with various RowComparators")
    public void testSorterWithComparators(RowComparator.SortType sortType) {
        Table tempTable = testTable.copy();

        RowComparator comparator = new RowComparator(sortType);
        if( comparator.getSortType() == SortType.PRICE_ASCENDING )
            tempTable = tempTable.first(10);

        LongWrapper time = null;
        if( sortType == SortType.PRICE_ASCENDING || sortType == SortType.PRICE_DESCENDING )
            time = new LongWrapper();

        Table sortedTable = Sorter.sortTable(tempTable, comparator, time);

        // Verify that the sorted table has the same number of rows as the original
        assert sortedTable.rowCount() == tempTable.rowCount() : "Sorted table should have the same number of rows as the original";

        // Verify that the sorted table is actually sorted according to the comparator
        assert SortingAnalysis.isSorted(sortedTable, comparator) : "Table should be sorted according to the " + sortType + " comparator";
    }

    @Test
    @DisplayName("Test sortTable with a null comparator")
    public void testSortTableWithNullComparator() {
        Table tempTable = Sorter.sortTable(testTable, null, null);
        assert TestUtils.equals(tempTable, testTable) : "Table should remain unchanged when comparator is null";
    }

    @Test
    @DisplayName("Test sortTable with the wrong type for the comparator (should not throw an exception, but should return the original table)")
    public void testSortTableWithWrongComparatorType() {
        Table tempTable = Sorter.sortTable(testTable, "", null);
        assert TestUtils.equals(tempTable, testTable) : "Table should remain unchanged when comparator is of the wrong type";
    }

}
