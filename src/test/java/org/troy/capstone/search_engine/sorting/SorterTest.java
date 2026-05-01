package org.troy.capstone.search_engine.sorting;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.TestUtils;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.search_engine.sorting.RowComparator.SortType;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.Row;
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

    @SuppressWarnings("deprecation")
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

        assert sortedTable.rowCount() == tempTable.rowCount() : "Sorted table should have the same number of rows as the original";

        assert SortingAnalysis.isSorted(sortedTable, comparator) : "Table should be sorted according to the " + sortType + " comparator";
    }

    @Test
    @DisplayName("Test sortTable with a null comparator")
    public void testSortTableWithNullComparator() {
        Table tempTable = Sorter.sortTable(testTable, null, null);
        assert TestUtils.equals(tempTable, testTable) : "Table should remain unchanged when comparator is null";
    }

    @SuppressWarnings("deprecation")
    @Test
    @DisplayName("Test mixedSort with a small list (should use insertion sort only)")
    public void testMixedSortWithSmallList() {
        Table tempTable = testTable.first(10);
        RowComparator comparator = new RowComparator(RowComparator.SortType.PRICE_ASCENDING);
        LongWrapper time = new LongWrapper();
        
        List<Row> rows = TableUtils.tableToRowList(tempTable);
        Table afterSortTable = tempTable.emptyCopy();
        Sorter.mixedSort(rows, comparator, time);
        rows.forEach(afterSortTable::append);
        
        assert time.getValue() > 0 : "Time should be recorded for sorting";
        assert SortingAnalysis.isSorted(afterSortTable, comparator) : "Table should be sorted according to the PRICE_ASCENDING comparator";
    }

    @SuppressWarnings("deprecation")
    @Test
    @DisplayName("Test mixedSort with a large list (should use both insertion sort and quick sort)")
    public void testMixedSortWithLargeList() {
        Table tempTable = testTable;
        RowComparator comparator = new RowComparator(RowComparator.SortType.PRICE_ASCENDING);
        LongWrapper time = new LongWrapper();

        List<Row> rows = TableUtils.tableToRowList(tempTable);
        Sorter.mixedSort(rows, comparator, time);
        Table afterSortTable = tempTable.emptyCopy();
        rows.forEach(afterSortTable::append);
        
        assert time.getValue() > 0 : "Time should be recorded for sorting";
        assert SortingAnalysis.isSorted(afterSortTable, comparator) : "Table should be sorted according to the PRICE_ASCENDING comparator";
    }

}