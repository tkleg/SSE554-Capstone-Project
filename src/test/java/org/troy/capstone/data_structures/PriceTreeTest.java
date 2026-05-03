package org.troy.capstone.data_structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.TableColumnName;

import tech.tablesaw.api.Table;

//Parameterized test info found on [9]

public class PriceTreeTest {
    private static PriceTree tree;
    private static final Table table = TestDataHolder.getTableCopy();

    @BeforeAll
    public static void setup() {
        tree = new PriceTree(table);
    }

    @Test
    @DisplayName("Ensure PriceTree is properly initialized")
    public void testPriceTreeInitialization() {
        assertNotNull(tree, "PriceTree should not be null");
        
        assertTrue(!tree.isEmpty(), "PriceTree should contain items after initialization");
        assertEquals( tree.size(), table.rowCount(), "PriceTree should contain the same number of items as the table rows" );
    }
  
    
    @ParameterizedTest
    @CsvSource({
        "0.0, 10.0, '0-10'",
        "10.0, 20.0, '10-20'", 
        "50.0, 250.0, '50-250'"
    })
    @DisplayName("Test PriceTree with various price ranges")
    public void testPriceTree(double minPrice, double maxPrice, String rangeDescription) {
        Table filteredTable = table.where(table.floatColumn(TableColumnName.PRICE.getColumnName()).isBetweenInclusive(minPrice, maxPrice));
        assertNotNull(filteredTable);
        
        int[] filteredByRange = tree.findItemsInPriceRange((float)minPrice, (float)maxPrice);
        assertNotNull(filteredByRange);

        assertEquals(filteredTable.rowCount(), filteredByRange.length, 
                    "Expected number of items in price range " + rangeDescription + " does not match");
    }
}
