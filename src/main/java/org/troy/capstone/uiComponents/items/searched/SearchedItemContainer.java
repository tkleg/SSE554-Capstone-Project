package org.troy.capstone.uiComponents.items.searched;

import java.util.List;

import org.troy.capstone.constants.tableColumns;
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
    }

    public void addItemPanel(SearchedItemPanel itemPanel) {
        itemContainer.getChildren().add(itemPanel);
    }

    public void addItemPanels(List<SearchedItemPanel> itemPanels) {
        itemContainer.getChildren().addAll(itemPanels);
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
}
