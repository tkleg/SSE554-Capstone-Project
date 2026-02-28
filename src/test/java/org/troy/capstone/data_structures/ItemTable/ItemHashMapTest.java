package org.troy.capstone.data_structures.ItemTable;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.entities.Item;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Table;


public class ItemHashMapTest {
    
    private static ItemHashMap map;
    private static Table table;

    @BeforeAll
    static void setup() {
        // We can use the fromTable method to create an ItemHashMap with optimized hash parameters for the test data
        table = TableUtils.readCleanedData();
        map = ItemHashMap.fromTable( table );
    }

    @Test
    @DisplayName("Test simple item retrieval")
    void testItem(){
        // Get the ID of the first item in the table
        String testId = table.stringColumn( tableColumns.ID.getColumnName() ).get(0);
        // Use the map to get the item with that ID
        Optional<Item> item = map.getItem( testId );

        assert item.isPresent() : "Item should be found in the map";
        assertEquals(item.get().getName(), 
            table.stringColumn( tableColumns.NAME.getColumnName() ).get(0), "Item name should match the name in the table");
    }

    @Test
    @DisplayName("Test item retrieval with non-existent ID")
    void testItemNotFound(){
        String nonExistentId = "non-existent-id";
        Optional<Item> item = map.getItem( nonExistentId );

        assert item.isEmpty() : "Item should not be found in the map for a non-existent ID";
    }


    @Test
    @DisplayName("Test table size to be 1000")
    void testBucketSizeDistribution(){
        assertEquals( 1000, map.size(), "ItemHashMap should contain 1000 items after initialization from the table" );
        assertEquals( 1000, table.rowCount(), "Table should contain 1000 rows" );
    }

    @Test
    @DisplayName("Test bucket distribution with optimized hash parameters")
    void testHashParametersSet(){
        // Just check that I and J are not null and are within the expected range (1 to PRIME-1 for I, 0 to PRIME-1 for J)
        List<String> itemIds = map.getItemIdsAsList();
        assert itemIds.size() > 0 : "There should be item IDs in the map";

        //Distribution depends slightly on the found I and J, which may differ
        IdHashKey.setI(new BigInteger("97110425") );
        IdHashKey.setJ(new BigInteger("43152856") );

        int[] customBucketDistribution = map.getFreshBucketSizeCount( itemIds, true );
        int[] builtInBucketDistribution = map.getFreshBucketSizeCount( itemIds, false );

        int[] expectedCustomDistribution = {1249, 626, 149, 21, 2, 1};
        int[] expectedBuiltInDistribution = {1280, 572, 165, 26, 5};

        // Check that we have at least the minimum expected length
        assert customBucketDistribution.length >= expectedCustomDistribution.length : 
            "Custom bucket distribution array should have at least " + expectedCustomDistribution.length + " elements. Actual length: " + customBucketDistribution.length;
        assert builtInBucketDistribution.length >= expectedBuiltInDistribution.length : 
            "Built-in bucket distribution array should have at least " + expectedBuiltInDistribution.length + " elements. Actual length: " + builtInBucketDistribution.length;

        for( int x = 0; x < expectedCustomDistribution.length; x++ ){
            assertEquals( expectedCustomDistribution[x], customBucketDistribution[x], 
                "Custom hash bucket distribution does not match expected distribution at index " + x );
        }
        
        for( int x = 0; x < expectedBuiltInDistribution.length; x++ ){
            assertEquals( expectedBuiltInDistribution[x], builtInBucketDistribution[x], 
                "Built-in hash bucket distribution does not match expected distribution at index " + x );
        }
    }
}
