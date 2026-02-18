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
            
        itemMap.printAllHashCodes();
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

}
