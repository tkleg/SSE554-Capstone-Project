package org.troy.capstone.search_engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.TableColumnName;

import tech.tablesaw.api.Table;

public class PriceFilterTest {
    private static PriceFilter priceFilter;
    private static final Table table = TestDataHolder.getTableCopy();

    @BeforeAll
    public static void setup() {
        priceFilter = new PriceFilter(table);
    }

    @ParameterizedTest
    @CsvSource({
        "0.0, 10.0, '0-10'",
        "10.0, 20.0, '10-20'", 
        "50.0, 250.0, '50-250'"
    })
    @DisplayName("Test PriceFilter with various price ranges")
    public void testPriceFilter(double minPrice, double maxPrice, String rangeDescription) {
        int[] filteredByRange = priceFilter.filterByPriceRange((float)minPrice, (float)maxPrice);
        assertNotNull(filteredByRange);

        Table filteredTable = table.where(table.floatColumn(TableColumnName.PRICE.getColumnName()).isBetweenInclusive(minPrice, maxPrice));
        assertNotNull(filteredTable);

        assertEquals(filteredTable.rowCount(), filteredByRange.length, 
                    "Expected number of items in price range " + rangeDescription + " does not match");
    }
}
