package org.troy.capstone.search_engine.sorting;

import org.troy.capstone.TestDataHolder;
import org.troy.capstone.search_engine.sorting.RowComparator.SortType;
import tech.tablesaw.api.Table;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.BeforeEach;

public class SorterTest {
    private Table testTable;

    @BeforeEach
    public void setup() {
        testTable = TestDataHolder.getTableCopy().first(50);
    }

    @ParameterizedTest
    @EnumSource(RowComparator.SortType.class)
    @DisplayName("Test Sorter with various RowComparators")
    public void testSorterWithComparators(RowComparator.SortType sortType) {
        RowComparator comparator = new RowComparator(sortType);
        if( comparator.getSortType() == SortType.PRICE_ASCENDING )
            testTable = testTable.first(10);
        
        Table sortedTable = Sorter.sortTable(testTable, comparator);

        // Verify that the sorted table has the same number of rows as the original
        assert sortedTable.rowCount() == testTable.rowCount() : "Sorted table should have the same number of rows as the original";

        // Verify that the sorted table is actually sorted according to the comparator
        assert SortingAnalysis.isSorted(sortedTable, comparator) : "Table should be sorted according to the " + sortType + " comparator";
    }

}
