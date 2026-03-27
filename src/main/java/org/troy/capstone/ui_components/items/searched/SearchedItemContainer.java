package org.troy.capstone.ui_components.items.searched;

import java.util.List;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.entities.Item;
import org.troy.capstone.managers.RecentlyViewedManager;
import org.troy.capstone.utils.UIUtils;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * The SearchedItemContainer class represents a UI component that contains and displays the search results as a list of SearchedItemPanel instances. It is a scrollable container that allows users to view all search results, and it provides methods to add new search result panels to the container.
 */
public class SearchedItemContainer extends ScrollPane {
    /** The container for all searched item panels */
    private final VBox itemContainer;
    

    /** The manager for recently viewed items, used to update the recently viewed items window when navigating through search results. */
    private final RecentlyViewedManager recentlyViewedManager;

    /**
     * Creates a SearchedItemContainer with a vertical box layout for displaying search result panels.
     * @param recentlyViewedManager The manager for recently viewed items, used to update the recently viewed items window when navigating through search results.
     */
    private SearchedItemContainer(RecentlyViewedManager recentlyViewedManager) {
        super();

        this.recentlyViewedManager = recentlyViewedManager;

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

    }

    /** Factory method to create a SearchedItemContainer with the given list of items.
     * @param items The list of items to display in the container.
     * @param recentlyViewedManager The manager for recently viewed items, used to update the recently viewed items window when navigating through search results.
     * @return A new instance of SearchedItemContainer populated with the given items.
     */
    public static SearchedItemContainer create(List<Item> items, RecentlyViewedManager recentlyViewedManager) {
        SearchedItemContainer container = new SearchedItemContainer(recentlyViewedManager);
        UIUtils.setSize(container, UISizeControl.SEARCHED_ITEM_CONTAINER_WIDTH.getValue(), UISizeControl.SEARCHED_ITEM_CONTAINER_HEIGHT.getValue());
        UIUtils.setLineBorder(container, 5, 1);
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
        }else if (items.isEmpty()) {
            stopAllImagesLoading();
            itemContainer.getChildren().clear();
            itemContainer.getChildren().add(new Label("No items found."));
        }else{
            stopAllImagesLoading();
            itemContainer.getChildren().clear();
            items.forEach(item -> {
                if (item != null)
                    addItemPanel(SearchedItemPanel.create(item, recentlyViewedManager));
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
