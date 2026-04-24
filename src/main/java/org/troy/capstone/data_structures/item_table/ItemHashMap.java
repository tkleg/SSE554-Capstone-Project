package org.troy.capstone.data_structures.item_table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.entities.Item;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

/**
 * Custom HashMap implementation for storing Items with their IDs as keys, using a custom universal hash function (IdHashKey) to optimize bucket distribution for the specific set of item IDs in our dataset.
 */
public class ItemHashMap extends HashMap<IdHashKey, Item> {

    //Main in EntryPoints.java to stop Javadocs from including it
    
    /** Maximum load factor for the hashmap. */
    private static final float MAX_LOAD_FACTOR = 0.75f;
    /** Table size for the hashmap, chosen to be a power of 2 for efficient modulo operations, and large enough to maintain a load factor of 0.75 for our expected number of items (961). */
    private static final int TABLE_SIZE = 2048;

    /**
     * Creates an ItemHashMap from a Table.
     * The map is initialized with the optimal hash parameters for the current item IDs.
     * 
     * @pre table is not null and contains the expected columns for creating Items (ID, Name, etc.), and contains no duplicate IDs.
     *
     * @param table A tablesaw Table containing the item data, with each row representing an item.
     * @return itemMap An ItemHashMap containing all items from the table, with hash parameters optimized for the item IDs in the table.
     */
    public static ItemHashMap fromTable(Table table) {
        ItemHashMap itemMap = new ItemHashMap(table.rowCount());
        System.out.println("P: " + IdHashKey.getP() + ", I: " + IdHashKey.getI() + ", J: " + IdHashKey.getJ());
        itemMap.addAllItems(table);
        return itemMap;
    }

    /**
     * Make initial capacity so that we have just over a 0.75 load factor.
     *
     * @pre data_size is a positive integer.
     *
     * @param data_size The number of items that will be added to the map, used to get a 0.75 load factor.
     */
    private ItemHashMap(int data_size) {
        super((int) (data_size / MAX_LOAD_FACTOR) + 1); // Calculate initial capacity based on expected data size and load factor
    }
    
    /**
     * Adds an item to the hashmap given a tablesaw Row.
     *
     * @pre itemRow is not null and contains the expected columns for creating an Item (ID, Name, etc.).
     *
     * @param itemRow A Row from a tablesaw Table containing item info.
     */
    private void addItem(Row itemRow) {
        String itemId = itemRow.getString(TableColumnName.ID.getColumnName());        
        put(new IdHashKey(itemId), Item.fromRow(itemRow));
    }

    /**
     * Adds all rows from a tablesaw Table to the hashmap as Items.
     *
     * @pre table is not null and contains the expected columns for creating Items (ID, Name, etc.), and contains no duplicate IDs.
     *
     * @param table A tablesaw Table with each row being an item to add to the map.
     */
    private void addAllItems(Table table) {
        table.stream().forEach(this::addItem);
        System.out.println("Finished adding items. Total items added: " + size());
    }

    /**
     * Retrieves an item from the hashmap given its ID.
     * Prints a message if the item is not found in the map.
     *
     * @pre itemId is not null and corresponds to a valid item ID in the map.
     *
     * @param itemId The ID of the item to retrieve.
     * @return An Optional containing the Item if found, or empty if not found.
     */
    public Optional<Item> getItem(String itemId) {
        IdHashKey key = new IdHashKey(itemId);
        Optional<Item> item = Optional.ofNullable(get(key));
        if (item.isEmpty())
            System.out.println("Item with ID " + itemId + " not found in ItemHashMap.");
        return item;
    }

    /**
     * Calculate bucket distribution with current I and J values (fresh hash calculation).
     *
     * @pre itemIds is not null and contains valid item IDs.
     * 
     * @param itemIds A list of item IDs to calculate the bucket distribution for.
     * @param useCustomHash Whether to use the custom IdHashKey hash function or the built-in String hashCode.
     * @return bucketSizeCounts An array where the value at index N is the number of buckets that have N items in them, according to the specified hash function.
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

    /**
     * Retrieves the keys of the hashmap as a list of IdHashKey objects.
     *
     * @return A list of IdHashKey objects representing the keys in the hashmap.
     */
    private List<IdHashKey> getKeysAsList() {
        return new ArrayList<>(keySet());
    }

    /**
     * Retrieves the item IDs of the hashmap as a list of strings.
     *
     * @return A list of strings representing the item IDs in the hashmap.
     */
    public List<String> getItemIdsAsList() {
        return getKeysAsList().stream()
            .map(IdHashKey::getValue)
            .collect(Collectors.toList());
    }

    /**
     * Prints a table comparing the distribution of bucket sizes (number of buckets with 0 items, 1 item, 2 items, etc.) for the custom hash function vs Java's built in String hashCode, using the same item IDs. 
     * This allows us to see how well our custom universal hash function is performing in terms of distributing
     * items across buckets compared to the built-in hash function.
     *
     * @pre findBestHashParameters() has already been called to optimize I and J for the current item IDs,
     * the internal state of the ItemHashMap is not modified between the two distribution calculations (i.e. no items are added or removed),
     * the same item IDs are used for both calculations,
     * and the map must be filled.
     */
    @SuppressWarnings("unused")
    private void printBucketSizeCountsCustomVsBuiltIn(){
        String col1 = "Entries in Bucket (N)", col2 = "Buckets with N entries (Custom Hash)", col3 = "Buckets with N entries (Built-in Hash)";
        System.out.printf("%-" + col1.length() + "s %s %-" + col2.length() + "s %s %-" + col3.length() + "s%n", col1, "|", col2, "|", col3);
        int[] customBucketSizeCounts = getFreshBucketSizeCount(getItemIdsAsList(), true); // Use fresh calculation with current I,J
        int[] builtInBucketSizeCounts = getFreshBucketSizeCount(getItemIdsAsList(), false); // Use fresh calculation for built-in String hash
        int maxSize = Math.max(customBucketSizeCounts.length, builtInBucketSizeCounts.length);
        customBucketSizeCounts = Arrays.copyOf(customBucketSizeCounts, maxSize); //Pad with zeros to match size
        builtInBucketSizeCounts = Arrays.copyOf(builtInBucketSizeCounts, maxSize); //Pad with zeros to match size
        for (int i = 0; i < maxSize; i++){
            int customCount = customBucketSizeCounts[i];
            int builtInCount = builtInBucketSizeCounts[i];
            System.out.printf("%-" + col1.length() + "d %s %-" + col2.length() + "d %s %-" + col3.length() + "d%n", i, "|", customCount, "|", builtInCount);
        }
    }

    /**
     * Creates a deep copy of the ItemHashMap.
     *
     * @return A new ItemHashMap containing the same entries as the original.
     * @throws CloneNotSupportedException if any of the keys or values in the map do not support cloning.
     */
    public ItemHashMap copy() throws CloneNotSupportedException {
        ItemHashMap copy = new ItemHashMap(size());
        for (Entry<IdHashKey, Item> entry : entrySet())
            copy.put((IdHashKey) entry.getKey().clone(), (Item) entry.getValue().clone());
        return copy;
    }

}
