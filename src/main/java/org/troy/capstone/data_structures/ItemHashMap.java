package org.troy.capstone.data_structures;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

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
        short testId = table.shortColumn("id").get(0); // Get the ID of the first item in the table for testing
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
        short itemId = itemRow.getShort("id");
        String tags = itemRow.getString("tags");
        tags = tags.substring(1, tags.length() - 1); // Remove parantheses bounding the tags list
        put(itemId, 
            Item.builder()
                .imageUrl( itemRow.getString("imageUrl") )
                .name( itemRow.getString("name") )
                .publisher( itemRow.getString("publisher") )
                .description( itemRow.getString("description") )
                .category( itemRow.getString("category") )
                .tags( new HashSet<>( Arrays.asList( tags.split(", ") ) ) )
                .price( itemRow.getFloat("price") )
                .reviewScore( itemRow.getFloat("reviewScore") )
                .reviewCount( itemRow.getShort("reviewCount") )
                .stockQuantity( itemRow.getShort("stockQuantity") )
                .id( itemId )
                .dateAdded( Converters.localDateToDate(itemRow.getDate("dateAdded")) )
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
