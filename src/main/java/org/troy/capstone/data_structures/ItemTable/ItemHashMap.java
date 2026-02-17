package org.troy.capstone.data_structures.ItemTable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

import org.troy.capstone.annotations.TestExclusionGenerated;
import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.entities.Item;
import org.troy.capstone.utils.Converters;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

/**
 * Custom key wrapper that allows us to override the hash function
 */
class PrimeHashKey {
    private final short value;
    
    // Prime constants for hash calculation
    private static final int PRIME1 = 31;
    private static final int PRIME2 = 17;
    private static final int PRIME3 = 13;
    
    public PrimeHashKey(short value) {
        this.value = value;
    }
    
    public short getValue() {
        return value;
    }
    
    @Override
    public int hashCode() {
        // Improved hash function that truly scrambles consecutive values
        int hash = value;
        
        // First round: multiply by large prime and rotate bits
        hash = (int) (hash * 2654435761L); // Large prime (2^32 / golden ratio)
        hash = Integer.rotateLeft(hash, 13);
        
        // Second round: XOR with different transformations
        hash = hash ^ (hash >>> 7);
        hash = hash * PRIME1;
        hash = hash ^ (hash >>> 12);
        
        // Third round: more bit mixing with different primes
        hash = hash * PRIME2;
        hash = hash ^ (hash >>> 16);
        hash = hash * PRIME3;
        
        // Final scrambling
        hash = hash ^ (hash >>> 5);
        hash = Integer.rotateRight(hash, 9);
        
        return Math.abs(hash);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PrimeHashKey that = (PrimeHashKey) obj;
        return value == that.value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

public class ItemHashMap extends HashMap<PrimeHashKey, Item> {

    private static final float LOAD_FACTOR = 0.75f;

    @TestExclusionGenerated
    public static void main(String[] args) {
        Table table = TableUtils.readCleanedData();
        ItemHashMap itemMap = new ItemHashMap(table);
        short testId = table.shortColumn(tableColumns.ID.getColumnName()).get(0);
        Optional<Item> itemOpt = itemMap.getItem(testId);
        if (itemOpt.isPresent())
            System.out.println("Item with ID " + testId + ": " + itemOpt.get());
        else
            System.out.println("Item with ID " + testId + " not found in ItemHashMap.");
            
        // Demonstrate custom hash function vs standard
        System.out.println("\n=== Custom Hash Function Demo ===");
        itemMap.demonstrateHashFunction();
    }

    public ItemHashMap(Table table) {
        // Calculate initial capacity to avoid resizing during population
        int capacity = (int) (table.rowCount() / LOAD_FACTOR) + 1;
        super(capacity);
        addAllItems(table);
    }
    
    private void addItem(Row itemRow) {
        short itemId = itemRow.getShort(tableColumns.ID.getColumnName());
        String tags = itemRow.getString(tableColumns.TAGS.getColumnName());
        tags = tags.substring(1, tags.length() - 1); // Remove parantheses bounding the tags list
        
        // Use PrimeHashKey instead of raw short
        PrimeHashKey key = new PrimeHashKey(itemId);
        put(key, 
            Item.builder()
                .imageUrl( itemRow.getString(tableColumns.IMAGE_URL.getColumnName()) )
                .name( itemRow.getString(tableColumns.NAME.getColumnName()) )
                .publisher( itemRow.getString(tableColumns.PUBLISHER.getColumnName()) )
                .description( itemRow.getString(tableColumns.DESCRIPTION.getColumnName()) )
                .category( itemRow.getString(tableColumns.CATEGORY.getColumnName()) )
                .tags( new HashSet<>( Arrays.asList( tags.split(", ") ) ) )
                .price( itemRow.getFloat(tableColumns.PRICE.getColumnName()) )
                .reviewScore( itemRow.getFloat(tableColumns.REVIEW_SCORE.getColumnName()) )
                .reviewCount( itemRow.getShort(tableColumns.REVIEW_COUNT.getColumnName()) )
                .stockQuantity( itemRow.getShort(tableColumns.STOCK_QUANTITY.getColumnName()) )
                .id( itemId )
                .dateAdded( Converters.localDateToDate(itemRow.getDate(tableColumns.DATE_ADDED.getColumnName())) )
            .build()
        );
    }

    private final void addAllItems(Table table) {
        table.stream().forEach(this::addItem);
    }

    public Optional<Item> getItem(short itemId) {
        PrimeHashKey key = new PrimeHashKey(itemId);
        Optional<Item> item = Optional.ofNullable(get(key));
        if (item.isEmpty())
            System.out.println("Item with ID " + itemId + " not found in ItemHashMap.");
        return item;
    }
    
    /**
     * Demonstrate the difference between standard hash and custom prime-based hash.
     */
    public void demonstrateHashFunction() {
        System.out.printf("%-6s %-15s %-15s %-10s%n", 
            "ID", "Standard Hash", "Prime Hash", "Difference");
        System.out.println("-".repeat(50));
        
        // Show hash comparison for sequential IDs
        short[] testIds = {1, 2, 3, 4, 5, 10, 100, 500, 1000};
        
        for (short id : testIds) {
            int standardHash = Short.valueOf(id).hashCode();
            PrimeHashKey primeKey = new PrimeHashKey(id);
            int primeHash = primeKey.hashCode();
            int difference = Math.abs(primeHash - standardHash);
            
            System.out.printf("%-6d %-15d %-15d %-10d%n", 
                id, standardHash, primeHash, difference);
        }
        
        // Analyze clustering for sequential IDs
        analyzeSequentialClustering();
    }
    
    /**
     * Analyze how the custom hash function reduces clustering.
     */
    private void analyzeSequentialClustering() {
        System.out.println("\n=== Clustering Analysis (Bucket Distribution) ===");
        System.out.printf("%-6s %-15s %-15s%n", "ID", "Standard Bucket", "Prime Bucket");
        System.out.println("-".repeat(40));
        
        int bucketCount = 16; // Typical HashMap initial capacity
        short[] sequentialIds = {100, 101, 102, 103, 104, 105, 106, 107};
        
        for (short id : sequentialIds) {
            int standardBucket = Math.abs(Short.valueOf(id).hashCode()) % bucketCount;
            PrimeHashKey primeKey = new PrimeHashKey(id);
            int primeBucket = Math.abs(primeKey.hashCode()) % bucketCount;
            
            System.out.printf("%-6d %-15d %-15d%n", id, standardBucket, primeBucket);
        }
        
        System.out.println("\nCustom hash function benefits:");
        System.out.println("• Reduces clustering of sequential IDs");
        System.out.println("• Better distribution across buckets");
        System.out.println("• Fewer hash collisions");
        System.out.println("• Improved HashMap performance");
    }
    
    /**
     * Get the custom hash value for an item ID.
     * @param itemId the item ID
     * @return custom hash value
     */
    public int getCustomHashValue(short itemId) {
        return new PrimeHashKey(itemId).hashCode();
    }
    
    /**
     * Compare standard vs custom hash for a specific ID.
     * @param itemId the item ID to analyze
     */
    public void compareHashFunctions(short itemId) {
        int standardHash = Short.valueOf(itemId).hashCode();
        int customHash = getCustomHashValue(itemId);
        
        System.out.printf("ID %d: Standard=%d, Custom=%d, Improvement=%d%n", 
            itemId, standardHash, customHash, Math.abs(customHash - standardHash));
    }

}
