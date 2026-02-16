package org.troy.capstone.uiComponents.items.searched;

import java.util.List;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

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
}
