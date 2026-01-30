package org.troy.capstone.uiMock;

import java.util.List;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class ItemScroller extends ScrollPane {
    private VBox itemContainer;
    
    public ItemScroller() {
        super();
        itemContainer = new VBox(5); // 5px spacing between items
        setContent(itemContainer);
        setFitToWidth(true);
    }

    public void addItemPanel(ItemPanel itemPanel) {
        itemContainer.getChildren().add(itemPanel);
    }

    public void addItemPanels(List<ItemPanel> itemPanels) {
        itemContainer.getChildren().addAll(itemPanels);
    }
}
