package org.troy.capstone.data_structures.ItemTable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.troy.capstone.annotations.TestExclusionGenerated;
import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.entities.Item;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class ItemHashMap extends HashMap<IdHashKey, Item> {

    private static final float MAX_LOAD_FACTOR = 0.75f;
    private static final int TABLE_SIZE = 2048;
    
    @TestExclusionGenerated
    public static void main(String[] args) {
        Table table = TableUtils.readCleanedData();
        ItemHashMap itemMap = fromTable(table);
        itemMap.printBucketSizeCountsCustomVsBuiltIn();
    }

    /*
    Creates an ItemHashMap from a Table.
    The map is initialized with the optimal hash parameters for the current item IDs.
    */
    public static ItemHashMap fromTable(Table table) {
        ItemHashMap itemMap = new ItemHashMap(table.rowCount());
        //itemMap.findBestHashParameters( table.stringColumn( tableColumns.ID.getColumnName() ).asList());
        System.out.println("Prime: " + IdHashKey.getPrime() + ", Best I: " + IdHashKey.getI() + ", Best J: " + IdHashKey.getJ());
        itemMap.addAllItems(table);
        return itemMap;
    }

    public ItemHashMap(int data_size) {
        super((int) (data_size / MAX_LOAD_FACTOR) + 1); // Calculate initial capacity based on expected data size and load factor
    }
    
    private void addItem(Row itemRow) {
        String itemId = itemRow.getString(tableColumns.ID.getColumnName());        
        put(new IdHashKey(itemId), Item.fromRow(itemRow));
    }

    private void addAllItems(Table table) {
        table.stream().forEach(this::addItem);
        System.out.println("Finished adding items. Total items added: " + size());
    }

    public Optional<Item> getItem(String itemId) {
        IdHashKey key = new IdHashKey(itemId);
        Optional<Item> item = Optional.ofNullable(get(key));
        if (item.isEmpty())
            System.out.println("Item with ID " + itemId + " not found in ItemHashMap.");
        return item;
    }

    /*
    Sets the I and J parameters for the universal hash function to optimize the distribution of items
        across buckets for the current item IDs.
    The map does not have to be filled before calling this, but the item IDs must be known (e.g. from the table) to find the best I and J values for those specific IDs.
    */
    private void findBestHashParameters( List<String> itemIds ){
        BigInteger bestI, bestJ, curI, curJ, 
            lowestI = BigInteger.valueOf(Integer.MAX_VALUE), lowestJ = BigInteger.valueOf(Integer.MAX_VALUE);
        int maxBucketsWithOneItem = 0;
        for( int iteration = 0; iteration < 10000; iteration++ ){// Try 10,000 times
            IdHashKey.reRoll_I_And_J();
            
            // Get the number of buckets that have exactly 1 item with the current I and J
            int bucketsWithOneItem = getFreshBucketSizeCount( itemIds, true )[1];

            curI = IdHashKey.getI();
            curJ = IdHashKey.getJ();
            if( bucketsWithOneItem > maxBucketsWithOneItem ){// If there are more buckets with one item than the current best, update best
                bestI = curI;
                bestJ = curJ;
                maxBucketsWithOneItem = bucketsWithOneItem;
                if( bestI.compareTo(lowestI) < 0 && bestJ.compareTo(lowestJ) < 0 ){// If the I and J are smaller than current best, update lowest
                    lowestI = bestI;
                    lowestJ = bestJ;
                }
            }
        }

        // Set I and J to the best found values to optimize the current map instance
        IdHashKey.setI(lowestI);
        IdHashKey.setJ(lowestJ);        
    }

    /*
    Recalculate bucket distribution with current I and J values (fresh hash calculation) 

    @param useCustomHash (boolean): whether to use the custom hash function or the built-in String hashCode
     
    @return int[] where the value at index N is the number of buckets that have N items in them, according to the specified hash function
    */
    public int[] getFreshBucketSizeCount( List<String> itemIds, boolean useCustomHash ){
        List<Integer> buckets = itemIds.stream() //List of the buckets that get hashed to
            .map( id -> useCustomHash ? new IdHashKey(id).hashCode() : id.hashCode() )
            .collect(Collectors.toList());
        int[] itemsInBucket = new int[TABLE_SIZE]; // Count of how many items get hashed to each bucket
        for (int bucket : buckets)
            itemsInBucket[Math.floorMod(bucket, TABLE_SIZE)]++;
        
        // Find the maximum bucket size to avoid ArrayIndexOutOfBoundsException
        int maxBucketSize = 0;
        for (int count : itemsInBucket)
            if (count > maxBucketSize)
                maxBucketSize = count;
        
        int[] bucketSizeCounts = new int[maxBucketSize + 1]; // Count of how many buckets have a certain size (0 items, 1 item, 2 items, etc.)
        for (int count : itemsInBucket)
            bucketSizeCounts[count]++;
        return bucketSizeCounts;
    }

    private List<IdHashKey> getKeysAsList() {
        return new ArrayList<>(keySet());
    }

    public List<String> getItemIdsAsList() {
        return getKeysAsList().stream()
            .map(IdHashKey::getValue)
            .collect(Collectors.toList());
    }

    /*
    Prints a table comparing the distribution of bucket sizes (number of buckets with 0 items, 1 item, 2 items, etc.) 
    for the custom hash function vs Java's built in String hashCode, using the same item IDs. 
    This allows us to see how well our custom universal hash function is performing in terms of distributing
    items across buckets compared to the built-in hash function.

    pre-conditions: findBestHashParameters() has already been called to optimize I and J for the current item IDs,
        and the internal state of the ItemHashMap is not modified between the two distribution calculations (i.e. no items are added or removed).
        Additionally, the same item IDs are used for both calculations. Finally, the map must be filled.
    */
    private void printBucketSizeCountsCustomVsBuiltIn(){
        String col1 = "Entries in Bucket (N)", col2 = "Buckets with N entries (Custom Hash)", col3 = "Buckets with N entries (Built-in Hash)";
        System.out.printf("%-" + col1.length() + "s %s %-" + col2.length() + "s %s %-" + col3.length() + "s%n", col1, "|", col2, "|", col3);
        int[] customBucketSizeCounts = getFreshBucketSizeCount(getItemIdsAsList(), true); // Use fresh calculation with current I,J
        int[] builtInBucketSizeCounts = getFreshBucketSizeCount(getItemIdsAsList(), false); // Use fresh calculation for built-in String hash
        int maxSize = Math.max(customBucketSizeCounts.length, builtInBucketSizeCounts.length);
        for (int i = 0; i < maxSize; i++){
            int customCount = i < customBucketSizeCounts.length ? customBucketSizeCounts[i] : 0;
            int builtInCount = i < builtInBucketSizeCounts.length ? builtInBucketSizeCounts[i] : 0;
            System.out.printf("%-" + col1.length() + "d %s %-" + col2.length() + "d %s %-" + col3.length() + "d%n", i, "|", customCount, "|", builtInCount);
        }
    }

}
