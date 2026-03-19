package org.troy.capstone.ui_components.items.searched;

import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * The SearchedItemContainer class represents a UI component that contains and displays the search results as a list of SearchedItemPanel instances. It is a scrollable container that allows users to view all search results, and it provides methods to add new search result panels to the container.
 */
public class SearchedItemContainer extends ScrollPane {
    /** The container for all searched item panels */
    private final VBox itemContainer;
    
    /**
     * Creates a SearchedItemContainer with a vertical box layout for displaying search result panels.
     */
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

    /**
     * Adds a new SearchedItemPanel to the container.
     * @param itemPanel The SearchedItemPanel to add.
     */
    public void addItemPanel(SearchedItemPanel itemPanel) {
        if( itemPanel != null )
            itemContainer.getChildren().add(itemPanel);
    }

}
