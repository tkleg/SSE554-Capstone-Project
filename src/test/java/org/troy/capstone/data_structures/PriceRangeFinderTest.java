package org.troy.capstone.data_structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Table;

// Parameterized test info found on https://www.baeldung.com/parameterized-tests-junit-5

public class PriceRangeFinderTest {
    private static PriceRangeFinder finder;
    private static Table table;

    @BeforeAll
    public static void setup() {
        table = TableUtils.readCleanedData();
        finder = new PriceRangeFinder(table);
    }

    @Test
    @DisplayName("Ensure PriceRangeFinder is properly initialized")
    public void testPriceRangeFinderInitialization() {
        assertNotNull(finder, "PriceRangeFinder should not be null");
        assertNotNull(table, "Table should not be null");
        
        assertTrue(!finder.isEmpty(), "PriceRangeFinder should contain items after initialization");
        assertEquals( finder.size(), table.rowCount(), "PriceRangeFinder should contain the same number of items as the table rows" );
    }
  
    
    @ParameterizedTest
    @CsvSource({
        "0.0, 10.0, '0-10'",
        "10.0, 20.0, '10-20'", 
        "50.0, 250.0, '50-250'"
    })
    @DisplayName("Test Price Range Finder with various price ranges")
    public void testPriceRangeFinder(double minPrice, double maxPrice, String rangeDescription) {
        Table filteredTable = table.where(table.floatColumn(tableColumns.PRICE.getColumnName()).isBetweenInclusive(minPrice, maxPrice));
        assertNotNull(filteredTable);
        
        int[] filteredByRange = finder.findItemsInPriceRange((float)minPrice, (float)maxPrice);
        assertNotNull(filteredByRange);

        assertEquals(filteredTable.rowCount(), filteredByRange.length, 
                    "Expected number of items in price range " + rangeDescription + " does not match");
    }
}
