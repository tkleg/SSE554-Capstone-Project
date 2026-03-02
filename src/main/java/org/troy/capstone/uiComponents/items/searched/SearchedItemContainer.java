package org.troy.capstone.uiComponents.items.searched;

import java.util.List;

import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.constants.uiSizeControls;
import org.troy.capstone.data_structures.ItemTable.ItemHashMap;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class SearchedItemContainer extends ScrollPane {
    private final VBox itemContainer;
    
    public SearchedItemContainer() {
        super();
        itemContainer = new VBox(5); // 5px spacing between items
        setContent(itemContainer);
        setFitToWidth(true);
        
        // Set fixed width to prevent content-based resizing
        setPrefWidth(uiSizeControls.SEARCHED_ITEM_CONTAINER_WIDTH);
        setMaxWidth(uiSizeControls.SEARCHED_ITEM_CONTAINER_WIDTH);
        setMinWidth(uiSizeControls.SEARCHED_ITEM_CONTAINER_WIDTH);
        
        //Optimize scroll performance
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setPannable(false);
        
        //Cache nodes to improve scroll performance
        itemContainer.setCache(true);
        itemContainer.setCacheHint(javafx.scene.CacheHint.SPEED);
    }

    public void addItemPanel(SearchedItemPanel itemPanel) {
        itemContainer.getChildren().add(itemPanel);
    }

    public void addItemPanels(List<SearchedItemPanel> itemPanels) {
        itemContainer.getChildren().addAll(itemPanels);
    }

    /**
     * Optimized method to add item directly from table row.
     * Avoids redundant row processing for better performance.
     */
    public void addItemFromRow(tech.tablesaw.api.Row row, ItemHashMap itemHashMap) {
        String itemId = row.getString(tableColumns.ID.getColumnName());
        itemHashMap.getItem(itemId).ifPresent(item -> {
            SearchedItemPanel itemPanel = new SearchedItemPanel(item);
            addItemPanel(itemPanel);
        });
    }

    public static SearchedItemContainer createFilledContainer(Table table, ItemHashMap itemHashMap) {
        SearchedItemContainer container = new SearchedItemContainer();
        for (Row row : table) {
            String itemId = row.getString(tableColumns.ID.getColumnName());
            SearchedItemPanel itemPanel = new SearchedItemPanel( itemHashMap.getItem(itemId).orElseThrow() );
            container.addItemPanel(itemPanel);
        }
        return container;
    }

    public void clearItems() {
        itemContainer.getChildren().clear();
    }
}
