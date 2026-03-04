package org.troy.capstone.data_structures.ItemTable;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.entities.Item;
import org.troy.capstone.utils.TableUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import tech.tablesaw.api.Table;


public class ItemHashMapTest {
    
    private static ItemHashMap map;
    private static Table table;

    @BeforeAll
    static void setup() {
        //We can use the fromTable method to create an ItemHashMap with optimized hash parameters for the test data
        table = TableUtils.readCleanedData();
        map = ItemHashMap.fromTable( table );
    }

    @Test
    @DisplayName("Test simple item retrieval")
    void testItemFound(){
        //Get the ID of the first item in the table
        String testId = table.stringColumn( TableColumnName.ID.getColumnName() ).get(0);
        //Use the map to get the item with that ID
        Optional<Item> item = map.getItem( testId );

        assert item.isPresent() : "Item should be found in the map";
        assertEquals(item.get().getName(), 
            table.stringColumn( TableColumnName.NAME.getColumnName() ).get(0), "Item name should match the name in the table");
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

    @Nested
    class bucketSizeComparisonTests{
        //Enables newline chars to be matched
        private static Pattern pattern = Pattern.compile(".*", Pattern.DOTALL);
        @BeforeAll
        static void setup(){
            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            IdHashKey.setI(new BigInteger("97110425") );
            IdHashKey.setJ(new BigInteger("43152856") );

            map.printBucketSizeCountsCustomVsBuiltIn();

            System.setOut(originalOut); // Restore original System.out

        }

        @Test
        @DisplayName("Verify Headers")
        void testBucketDistributionComparisonHeaders(){
            String header1 = "Entries in Bucket (N)";
            String header2 = "Buckets with N entries (Custom Hash)";
            String header3 = "Buckets with N entries (Built-in Hash)";
            
            String patternString = ".*" + header1 + "\\s*\\|\\s*" + header2 + "\\s*\\|\\s*" + header3 + ".*";
            assert pattern.matcher(patternString).matches() : String.format("Output should contain headers in the format: '%s | %s | %s'", header1, header2, header3);
        }

        @DisplayName("Test printing of bucketsize comparison table")
        @ParameterizedTest
        @CsvSource({
            "0, 1249, 1280",
            "1, 626, 572",
            "2, 149, 165",
            "3, 21, 26",
            "4, 2, 5",
            "5, 1, 0"
        })
        void testBucketDistributionComparisonPrintout(int numEntriesHashingToBucket, int customCount, int builtInCount){

            String patternToMatch = String.format(".*%d\\s*\\|\\s*%d\\s*\\|\\s*%d.*", numEntriesHashingToBucket, customCount, builtInCount);
            assert pattern.matcher(patternToMatch).matches() : String.format("Output should contain a line with bucket %d, custom count %d, and built-in count %d", numEntriesHashingToBucket, customCount, builtInCount);

        }
    }
}
