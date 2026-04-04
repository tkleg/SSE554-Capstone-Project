package org.troy.capstone.ui_components.items.searched;

import java.util.List;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.data_structures.SearchedItemsLinkedList;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.entities.Item;
import org.troy.capstone.managers.RecentlyViewedManager;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The SearchedItemPagination class represents a UI component that provides pagination for search results.
 * It allows users to navigate through multiple pages of search results, with buttons to go back and forth.
 */
public class SearchedItemPagination extends VBox {

    /** The item hash map containing all items, used to populate the pagination content. */
    private final ItemHashMap itemHashMap;

    /** The linked list representing the pages of search results. */
    private SearchedItemsLinkedList pageList;

    /** The SearchedItemContainer instance that displays the current page of search results. */
    private final SearchedItemContainer mySearchedItemContainer;

    /** The button to navigate to the previous page of search results. */
    private final Button prevButton;

    /** The button to navigate to the next page of search results. */
    private final Button nextButton;

    /**
     * Constructor for SearchedItemPagination. Initializes the item hash map and sets up the pagination component.
     * @pre itemHashMap should contain valid item data to populate the pagination content.
     * @param itemHashMap The item hash map containing all items, used to populate the pagination content based on the current search results.
     * @param recentlyViewedManager The manager for recently viewed items, used to update the recently viewed items window when navigating through search results.
    */
    public SearchedItemPagination(ItemHashMap itemHashMap, RecentlyViewedManager recentlyViewedManager) {

        this.itemHashMap = itemHashMap;

        List<String> initialIds = itemHashMap.getItemIdsAsList();

        HBox buttonContainer = new HBox(UISizeControl.SEARCHED_ITEM_PAGINATION_BUTTON_SPACING.getValue());
        buttonContainer.setAlignment(Pos.CENTER);
        prevButton = new Button("Previous");
        nextButton = new Button("Next");

        setSpacing(UISizeControl.HEIGHT_PADDING.getValue());

        pageList = new SearchedItemsLinkedList(itemHashMap, initialIds);
        mySearchedItemContainer = SearchedItemContainer.create(pageList.getHead(), recentlyViewedManager);
        this.getChildren().clear();
        this.setAlignment(Pos.TOP_CENTER);
        this.getChildren().add(mySearchedItemContainer);
        buttonContainer.getChildren().addAll(prevButton, nextButton);
        this.getChildren().add(buttonContainer);

        prevButton.setOnAction(event -> { mySearchedItemContainer.updateItems(getPreviousPage()); });
        nextButton.setOnAction(event -> { mySearchedItemContainer.updateItems(getNextPage()); });

    }

     /**
      * Updates the pagination content with a new list of item IDs corresponding to search results.
      * Retrieves the corresponding items from the item hash map and updates the displayed content accordingly.
       * Logs a message if the pagination component is not found or if there is a type error with the component.
      * 
      * @pre A searched item pagination component should be added to the manager with the expected key and type before this method is called.
      *      itemIDs should be a list of valid item IDs corresponding to search results.
      * @param itemIdList A list of item IDs corresponding to search results to update the pagination component with.
     */
    public final void update(List<String> itemIdList) {
        pageList = new SearchedItemsLinkedList(itemHashMap, itemIdList);
        mySearchedItemContainer.updateItems( pageList.getHead() );
    }

    /**
     * Returns the list of items in the previous page of search results.
     * @pre The pagination component should be properly initialized and the linked list should be set up to allow for navigating to the previous page.
     * @post If there is a previous page, the current page is updated to the previous page and the corresponding items are returned. If there is no previous page, null is returned.
     * @return The list of items in the previous page of search results, or null if there is no previous page.
     */
    private List<Item> getPreviousPage() {
        return pageList.getPrevious();
    }

    /**
     * Returns the list of items in the next page of search results.
     * @pre The pagination component should be properly initialized and the linked list should be set up to allow for navigating to the next page.
     * @post If there is a next page, the current page is updated to the next page and the corresponding items are returned. If there is no next page, null is returned.
     * @return The list of items in the next page of search results, or null if there is no next page.
     */
    private List<Item> getNextPage() {
        return pageList.getNext();
    }
    
}
