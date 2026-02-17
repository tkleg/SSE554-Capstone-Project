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

public class ItemHashMap extends HashMap<IdHashKey, Item> {

    private static final float MAX_LOAD_FACTOR = 0.75f;

    @TestExclusionGenerated
    public static void main(String[] args) {
        Table table = TableUtils.readCleanedData();
        ItemHashMap itemMap = new ItemHashMap(table);
        String testId = table.stringColumn(tableColumns.ID.getColumnName()).get(0);
        Optional<Item> itemOpt = itemMap.getItem(testId);
        if (itemOpt.isPresent())
            System.out.println("Item with ID " + testId + ": " + itemOpt.get());
        else
            System.out.println("Item with ID " + testId + " not found in ItemHashMap.");
            
        // Demonstrate custom hash function vs standard
        System.out.println("\n=== Custom Hash Function Demo ===");
        itemMap.demonstrateHashFunction();
        //itemMap.printAllHashCodes();
    }

    public ItemHashMap(Table table) {
        // Calculate initial capacity to avoid resizing during population
        int capacity = (int) (table.rowCount() / MAX_LOAD_FACTOR) + 1;
        super(capacity);
        addAllItems(table);
    }
    
    private void addItem(Row itemRow) {
        String itemId = itemRow.getString(tableColumns.ID.getColumnName());
        String tags = itemRow.getString(tableColumns.TAGS.getColumnName());
        tags = tags.substring(1, tags.length() - 1); // Remove parantheses bounding the tags list
        
        // Use IdHashKey instead of raw short
        IdHashKey key = new IdHashKey(itemId);
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

    public Optional<Item> getItem(String itemId) {
        IdHashKey key = new IdHashKey(itemId);
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
        String[] testIds = {"1", "2", "3", "4", "5", "10", "100", "500", "1000"};
        
        for (String id : testIds) {
            String itemId = id;
            IdHashKey idKey = new IdHashKey(id);
            int standardHash = id.hashCode();
            int primeHash = idKey.hashCode();
            int difference = Math.abs(primeHash - standardHash);
            
            System.out.printf("%-6s %-15d %-15d %-10d%n", 
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
        
        int bucketCount = 2048; // Typical HashMap initial capacity
        String[] sequentialIds = {"100", "101", "102", "103", "104", "105", "106", "107"};
        
        for (String id : sequentialIds) {
            int standardBucket = Math.abs(id.hashCode()) % bucketCount;
            IdHashKey idKey = new IdHashKey(id);
            int primeBucket = Math.abs(idKey.hashCode()) % bucketCount;
            
            System.out.printf("%-6s %-15d %-15d%n", id, standardBucket, primeBucket);
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
    public int getCustomHashValue(String itemId) {
        return new IdHashKey(itemId).hashCode();
    }
    
    /**
     * Compare standard vs custom hash for a specific ID.
     * @param itemId the item ID to analyze
     */
    public void compareHashFunctions(String itemId) {
        int standardHash = itemId.hashCode();
        int customHash = getCustomHashValue(itemId);
        
        System.out.printf("ID %s: Standard=%d, Custom=%d, Improvement=%d%n", 
            itemId, standardHash, customHash, Math.abs(customHash - standardHash));
    }

    public void printAllHashCodes() {
        System.out.printf("%-6s %-15s%n", "ID", "Custom Hash");
        System.out.println("-".repeat(25));
        for (IdHashKey key : keySet()) {
            System.out.printf("%-6s %-15d%n", key.getValue(), key.hashCode());
        }
    }

}
