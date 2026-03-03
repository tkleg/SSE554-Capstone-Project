package org.troy.capstone.uiComponents.items.searched;

import java.util.List;

import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.constants.uiSizeControls;
import org.troy.capstone.data_structures.ItemTable.ItemHashMap;
import org.troy.capstone.utils.UIUtils;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class SearchedItemContainer extends ScrollPane {
    private final VBox itemContainer;
    
    public static SearchedItemContainer create(){
        SearchedItemContainer container = new SearchedItemContainer();
        UIUtils.setSize(container, uiSizeControls.SEARCHED_ITEM_CONTAINER_WIDTH, uiSizeControls.SEARCHED_ITEM_CONTAINER_HEIGHT);
        return container;
    }

    public SearchedItemContainer() {
        super();
        itemContainer = new VBox(5); // 5px spacing between items
        itemContainer.setAlignment(Pos.TOP_CENTER); // Center-align items consistently
        setContent(itemContainer);
        setFitToWidth(true);

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
    public void addItemFromRow(Row row, ItemHashMap itemHashMap) {
        String itemId = row.getString(TableColumnName.ID.getColumnName());
        itemHashMap.getItem(itemId).ifPresent(item -> {
            SearchedItemPanel itemPanel = new SearchedItemPanel(item);
            addItemPanel(itemPanel);
        });
    }

    public static SearchedItemContainer createFilledContainer(Table table, ItemHashMap itemHashMap) {
        SearchedItemContainer container = new SearchedItemContainer();
        for (Row row : table) {
            String itemId = row.getString(TableColumnName.ID.getColumnName());
            SearchedItemPanel itemPanel = new SearchedItemPanel( itemHashMap.getItem(itemId).orElseThrow() );
            container.addItemPanel(itemPanel);
        }
        return container;
    }

    public void clearItems() {
        itemContainer.getChildren().clear();
    }
}
