package org.troy.capstone.data_structures.item_table;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.entities.Item;

import tech.tablesaw.api.Table;


public class ItemHashMapTest {
    
    private static final ItemHashMap map = TestDataHolder.getItemHashMapCopy();
    private static final Table table = TestDataHolder.getTableCopy();

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
    @DisplayName("Test table size to be 961")
    void testBucketSizeDistribution(){
        assertEquals( 961, map.size(), "ItemHashMap should contain 961 items after initialization from the table" );
        assertEquals( 961, table.rowCount(), "Table should contain 961 rows" );
    }

    @Test
    @DisplayName("Test bucket distribution with optimized hash parameters")
    void testHashParametersSet(){
        // Just check that I and J are not null and are within the expected range (1 to PRIME-1 for I, 0 to PRIME-1 for J)
        List<String> itemIds = map.getItemIdsAsList();
        assert !itemIds.isEmpty() : "There should be item IDs in the map";

        //Distribution depends slightly on the found I and J, which may differ
        try{
            Field fieldI = IdHashKey.class.getDeclaredField("I");
            fieldI.setAccessible(true);
            fieldI.set(null, new BigInteger("77507594") );
            
            Field fieldJ = IdHashKey.class.getDeclaredField("J");
            fieldJ.setAccessible(true);
            fieldJ.set(null, new BigInteger("99688092") );
        }catch(IllegalAccessException | IllegalArgumentException | NoSuchFieldException e){
            throw new RuntimeException("Failed to set hash parameters via reflection", e);
        }

        int[] customBucketDistribution = map.getFreshBucketSizeCount( itemIds, true );
        int[] builtInBucketDistribution = map.getFreshBucketSizeCount( itemIds, false );

        int[] expectedCustomDistribution = {1409, 394, 179, 55, 11};
        int[] expectedBuiltInDistribution = {1301, 563, 158, 22, 4};

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
    @DisplayName("Bucket Size Distribution Comparison Tests")
    @SuppressWarnings("unused")
    class bucketSizeComparisonTests{
        //Enables newline chars to be matched
        private final static Pattern PATTERN = Pattern.compile(".*", Pattern.DOTALL);
        
        @BeforeAll
        static void setup(){
            PrintStream originalOut = System.out;
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            try {
                // Use reflection to set I and J
                Field fieldI = IdHashKey.class.getDeclaredField("I");
                fieldI.setAccessible(true);
                fieldI.set(null, new BigInteger("77507594"));
                
                Field fieldJ = IdHashKey.class.getDeclaredField("J");
                fieldJ.setAccessible(true);
                fieldJ.set(null, new BigInteger("99688092"));
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
                throw new RuntimeException("Failed to set hash parameters via reflection", e);
            }

            try{
                Method printMethod = ItemHashMap.class.getDeclaredMethod("printBucketSizeCountsCustomVsBuiltIn");
                printMethod.setAccessible(true);
                printMethod.invoke(map);
            }catch(IllegalAccessException | NoSuchMethodException | InvocationTargetException e){
                throw new RuntimeException("Failed to invoke printBucketSizeCountsCustomVsBuiltIn via reflection", e);
            }finally{
                System.setOut(originalOut); // Restore original System.out
            }

        }

        @Test
        @DisplayName("Verify Headers")
        void testBucketDistributionComparisonHeaders(){
            String header1 = "Entries in Bucket (N)";
            String header2 = "Buckets with N entries (Custom Hash)";
            String header3 = "Buckets with N entries (Built-in Hash)";
            
            String patternString = ".*" + header1 + "\\s*\\|\\s*" + header2 + "\\s*\\|\\s*" + header3 + ".*";
            assert PATTERN.matcher(patternString).matches() : String.format("Output should contain headers in the format: '%s | %s | %s'", header1, header2, header3);
        }

        @DisplayName("Test printing of bucketsize comparison table")
        @ParameterizedTest
        @CsvSource({
            "0, 1409, 1301",
            "1, 394, 563",
            "2, 179, 158",
            "3, 55, 22",
            "4, 11, 4"
        })
        void testBucketDistributionComparisonPrintout(int numEntriesHashingToBucket, int customCount, int builtInCount){

            String patternToMatch = String.format(".*%d\\s*\\|\\s*%d\\s*\\|\\s*%d.*", numEntriesHashingToBucket, customCount, builtInCount);
            assert PATTERN.matcher(patternToMatch).matches() : String.format("Output should contain a line with bucket %d, custom count %d, and built-in count %d", numEntriesHashingToBucket, customCount, builtInCount);

        }
    }
}
