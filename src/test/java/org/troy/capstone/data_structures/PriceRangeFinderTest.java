package org.troy.capstone.data_structures;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Table;

public class PriceRangeFinderTest {
    private static PriceRangeFinder finder;
    private static Table table;

    @BeforeAll
    public static void setup() {
        table = TableUtils.readCleanedData();
        finder = new PriceRangeFinder(table);
    }

    @Test
    @DisplayName("Test Price Range Finder with range 10-20")
    public void testPriceRangeFinder10_20() {
        Table filteredTable = table.where(table.floatColumn("price").isBetweenInclusive(10.0, 20.0));
        assertNotNull(filteredTable);
        
        List<Short> filteredByRange = finder.findItemsInPriceRange(10.0f, 20.0f);
        assertNotNull(filteredByRange);

        assertEquals(filteredTable.rowCount(), filteredByRange.size(), "Expected number of items in price range 10-20 does not match");
    }
}
