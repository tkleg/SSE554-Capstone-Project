package org.troy.capstone.ui_components.items.searched;

import java.util.List;

import org.troy.capstone.data_structures.ItemTable.ItemHashMap;
import org.troy.capstone.data_structures.SearchedItemsLinkedList;
import org.troy.capstone.entities.Item;
import org.troy.capstone.managers.GeneralManager;
import org.troy.capstone.constants.UIElementName;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * The SearchedItemPagination class represents a UI component that provides pagination for search results.
 * It allows users to navigate through multiple pages of search results, displaying a fixed number of items per page.
 */
public class SearchedItemPagination extends VBox {

    private ItemHashMap itemHashMap;

    private SearchedItemsLinkedList pageList;

    private SearchedItemContainer mySearchedItemContainer;

    private final Button prevButton, nextButton;

    /**
     * Constructor for SearchedItemPagination. Initializes the item hash map and sets up the pagination component.
     * @pre itemHashMap should contain valid item data to populate the pagination content.
     * @param itemHashMap The item hash map containing all items, used to populate the pagination content based on the current search results.
    */
    private SearchedItemPagination(ItemHashMap itemHashMap) {

        this.itemHashMap = itemHashMap;

        List<String> initialIds = itemHashMap.getItemIdsAsList();

        prevButton = new Button("Previous");
        nextButton = new Button("Next");

        pageList = new SearchedItemsLinkedList(itemHashMap, initialIds);
        mySearchedItemContainer = new SearchedItemContainer(pageList.getHead());
        this.getChildren().clear();
        this.getChildren().add(mySearchedItemContainer);
        this.getChildren().addAll(prevButton, nextButton);

        prevButton.setOnAction(event -> { showPreviousPage(); });

        nextButton.setOnAction(event -> { showNextPage(); });

    }

    public static SearchedItemPagination create(ItemHashMap itemHashMap, GeneralManager generalManager) {
        SearchedItemPagination pagination =  new SearchedItemPagination(itemHashMap);
        generalManager.addUIElement(UIElementName.SEARCHED_ITEM_PAGINATION, pagination);
        return pagination;
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

    private void showPreviousPage() {
        List<Item> previousItems = pageList.getPrevious();
        if (previousItems != null) {
            mySearchedItemContainer.updateItems(previousItems);
        }
    }

    private void showNextPage() {
        List<Item> nextItems = pageList.getNext();
        if (nextItems != null) {
            mySearchedItemContainer.updateItems(nextItems);
        }
    }
    
}
