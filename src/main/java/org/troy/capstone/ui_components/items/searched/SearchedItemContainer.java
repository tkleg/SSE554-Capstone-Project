package org.troy.capstone.ui_components.items.searched;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class SearchedItemContainer extends ScrollPane {
    private final VBox itemContainer;
    
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
        if( itemPanel != null )
            itemContainer.getChildren().add(itemPanel);
    }

}
