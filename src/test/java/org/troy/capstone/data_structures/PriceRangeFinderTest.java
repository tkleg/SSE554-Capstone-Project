package org.troy.capstone.data_structures;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest
    @CsvSource({
        "0.0, 10.0, '0-10'",
        "10.0, 20.0, '10-20'", 
        "50.0, 250.0, '50-250'"
    })
    @DisplayName("Test Price Range Finder with various price ranges")
    public void testPriceRangeFinder(double minPrice, double maxPrice, String rangeDescription) {
        Table filteredTable = table.where(table.floatColumn("price").isBetweenInclusive(minPrice, maxPrice));
        assertNotNull(filteredTable);
        
        List<Short> filteredByRange = finder.findItemsInPriceRange((float)minPrice, (float)maxPrice);
        assertNotNull(filteredByRange);

        // Debug output for the failing test case
        if (minPrice == 50.0 && maxPrice == 250.0) {
            System.out.println("=== DEBUG INFO FOR RANGE " + rangeDescription + " ===");
            System.out.println("Table filter found: " + filteredTable.rowCount() + " items");
            System.out.println("PriceRangeFinder found: " + filteredByRange.size() + " items");
            
            // Check for duplicate prices in the filtered table
            var prices = filteredTable.floatColumn("price").asList();
            long uniquePrices = prices.stream().distinct().count();
            System.out.println("Unique prices in table: " + uniquePrices);
            System.out.println("Total prices in table: " + prices.size());
            
            if (uniquePrices < prices.size()) {
                System.out.println("WARNING: Duplicate prices detected! This may explain the discrepancy.");
            }
        }

        assertEquals(filteredTable.rowCount(), filteredByRange.size(), 
                    "Expected number of items in price range " + rangeDescription + " does not match");
    }
}
