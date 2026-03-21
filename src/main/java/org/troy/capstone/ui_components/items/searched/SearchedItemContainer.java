package org.troy.capstone.ui_components.items.searched;

import java.util.List;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.entities.Item;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * The SearchedItemContainer class represents a UI component that contains and displays the search results as a list of SearchedItemPanel instances. It is a scrollable container that allows users to view all search results, and it provides methods to add new search result panels to the container.
 */
public class SearchedItemContainer extends ScrollPane {
    /** The container for all searched item panels */
    private final VBox itemContainer;
    
    /**
     * Creates a SearchedItemContainer with a vertical box layout for displaying search result panels.
     */
    private SearchedItemContainer() {
        super();
        itemContainer = new VBox(UISizeControl.SEARCHED_ITEM_PANEL_SPACING.getValue()); // 5px spacing between items
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

        setBorder(new Border(new BorderStroke(
            Color.BLACK, 
            BorderStrokeStyle.SOLID, 
            new CornerRadii(5), 
            new BorderWidths(1)
        )));
    }

    /** Factory method to create a SearchedItemContainer with the given list of items.
     * @param items The list of items to display in the container.
     * @return A new instance of SearchedItemContainer populated with the given items.
     */
    public static SearchedItemContainer create(List<Item> items) {
        SearchedItemContainer container = new SearchedItemContainer();
        container.updateItems(items);
        return container;
    }

    /**
     * Adds a new SearchedItemPanel to the container.
     * 
     * @pre itemPanel is not null and is properly initialized with the data to display for a search result.
     * 
     * @post If itemPanel is not null, it is added to the itemContainer and becomes visible in the UI.
     * @param itemPanel The SearchedItemPanel to add.
     */ 
    private void addItemPanel(SearchedItemPanel itemPanel) {
        if( itemPanel != null )
            itemContainer.getChildren().add(itemPanel);
    }

    /** Updates the items displayed in the container with a new list of items.
     * @pre items should be a valid list of Item objects to display in the container. The SearchedItemContainer should be properly initialized to allow for updating the displayed items.
     * @post The itemContainer is cleared and repopulated with new SearchedItemPanel instances corresponding to the provided list of items. If the list is null or empty, a message indicating that no items were found is displayed instead.
     * @param items The new list of items to display in the container.
     */
    public final void updateItems(List<Item> items) {
        if( items == null ){
            System.out.println("Warning: updateItems called with null list. Doing nothing.");
            return;
        }else if (items.isEmpty()) {
            stopAllImagesLoading();
            itemContainer.getChildren().clear();
            itemContainer.getChildren().add(new Label("No items found."));
            return;
        }else{
            stopAllImagesLoading();
            itemContainer.getChildren().clear();
            items.forEach(item -> {
                if (item != null)
                    addItemPanel(new SearchedItemPanel(item));
            });
        }
    }

    /**
     * Stops all image loading tasks from running
     * 
     * @post All asynchronous image loading tasks for the SearchedItemPanel instances currently displayed in the container are stopped, preventing any further loading of images that are not being displayed.
     */
    public void stopAllImagesLoading() {
        itemContainer.getChildren().forEach(node -> {
            ((SearchedItemPanel) node).stopLoadingImage();
        });
    }

}
