package org.troy.capstone.data_structures.ItemTable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
            
        itemMap.printNumBucketsForEachSize();
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
     * Get the custom hash value for an item ID.
     * @param itemId the item ID
     * @return custom hash value
     */
    public int getCustomHashValue(String itemId) {
        return new IdHashKey(itemId).hashCode();
    }

    public void printAllHashCodes() {
        System.out.printf("%-6s %-15s%n", "ID", "Custom Hash");
        System.out.println("-".repeat(25));
        for (IdHashKey key : keySet()) {
            System.out.printf("%-6s %-15d%n", key.getValue(), key.hashCode());
        }
    }

    public void printNumBucketsForEachSize(){
        List<Integer> buckets = keySet().stream() //List of the buckets that get hashed to
            .map( key -> key.hashCode() )
            .collect(Collectors.toList());
        int[] bucketCounts = new int[2048]; // Count of how many items get hashed to each bucket
        for (int bucket : buckets)
            bucketCounts[bucket]++;
        
        // Find the maximum bucket size to avoid ArrayIndexOutOfBoundsException
        int maxBucketSize = 0;
        for (int count : bucketCounts)
            if (count > maxBucketSize)
                maxBucketSize = count;
        
        int[] bucketSizeCounts = new int[maxBucketSize + 1]; // Count of how many buckets have a certain size (0 items, 1 item, 2 items, etc.)
        for (int count : bucketCounts)
            bucketSizeCounts[count]++;
        int backwardsIndexFirstNonZero = 0;
        for (int i = bucketSizeCounts.length - 1; i >= 0; i--){
            if (bucketSizeCounts[i] != 0){
                backwardsIndexFirstNonZero = i;
                break;
            }
        }
        System.out.println("Prime = " + IdHashKey.getPrime() + ", I = " + IdHashKey.getI() + ", J = " + IdHashKey.getJ());
        System.out.printf("%-17s %s %-15s%n", "Entries in Bucket", "|",  "Num Buckets with that many Entries");
        for (int i = 0; i <= backwardsIndexFirstNonZero; i++)
            System.out.printf("%-17d %s %-15d%n", i, "|", bucketSizeCounts[i]);
    }

}
