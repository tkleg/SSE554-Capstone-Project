package org.troy.capstone.data_structures;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.entities.Item;
import org.troy.capstone.utils.Converters;
import org.troy.capstone.utils.TableUtils;

import javafx.application.Platform;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class ItemHashMap extends HashMap<Short, Item> {

    public static void main(String[] args) {
        // Initialize JavaFX Platform
        // Needed to create Image objects for Items, which requires JavaFX to be initialized
        Platform.startup(() -> {});
        
        Table table = TableUtils.readCleanedData();
        ItemHashMap itemMap = new ItemHashMap(table);
        short testId = table.shortColumn(tableColumns.ID.getColumnName()).get(0); // Get the ID of the first item in the table for testing
        Optional<Item> itemOpt = itemMap.getItem(testId);
        itemOpt.ifPresentOrElse(
            item -> System.out.println("Item with ID " + testId + ": " + item),
            () -> System.out.println("Item with ID " + testId + " not found.")
        );
        
        // Exit JavaFX Platform
        Platform.exit();
    }

    public ItemHashMap(Table table) {
        addAllItems(table);
    }

    private void addItem(Row itemRow) {
        short itemId = itemRow.getShort(tableColumns.ID.getColumnName());
        String tags = itemRow.getString(tableColumns.TAGS.getColumnName());
        tags = tags.substring(1, tags.length() - 1); // Remove parantheses bounding the tags list
        put(itemId, 
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
        Optional<Item> item = Optional.ofNullable(get(itemId));
        if (item.isEmpty())
            System.out.println("Item with ID " + itemId + " not found in ItemHashMap.");
        return item;
    }

}
