package org.troy.capstone.ui_components.items.searched;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.data_structures.ItemTable.IdHashKey;
import org.troy.capstone.data_structures.ItemTable.ItemHashMap;
import org.troy.capstone.managers.GeneralManager;
import org.troy.capstone.utils.UIUtils;

import javafx.scene.control.Label;
import javafx.scene.control.Pagination;

/**
 * The SearchedItemPagination class represents a UI component that provides pagination for search results.
 * It allows users to navigate through multiple pages of search results, displaying a fixed number of items per page.
 */
public class SearchedItemPagination extends Pagination {
    /** The number of items to display per page in the pagination */
    private static final int ITEMS_PER_PAGE = 10;
    
    /** The item hash map containing all items, used to populate the pagination content based on the current search results. */
    private final ItemHashMap itemHashMap;

    /** A label to display when no items are found in the search results. */
    private static final Label EMPTY_LABEL = new Label("No items found.");

    /**
     * Constructor for SearchedItemPagination. Initializes the item hash map and sets up the pagination component.
     * @pre itemHashMap should contain valid item data to populate the pagination content.
     * @param itemHashMap The item hash map containing all items, used to populate the pagination content based on the current search results.
    */
    private SearchedItemPagination(ItemHashMap itemHashMap) {
        this.itemHashMap = itemHashMap;
    }

    /**
     * Factory method to create a SearchedItemPagination with the appropriate size and add it to the UIElementManager.
     * 
     * @pre itemHashMap should contain valid item data to populate the pagination content.
     *      generalManager should be properly initialized to allow for adding the created SearchedItemPagination to it.
     * 
     * @param itemHashMap The item hash map containing all items, used to populate the pagination content.
     * @param generalManager The general manager to add the created SearchedItemPagination to for access by other components.
     * @return The created SearchedItemPagination instance with content populated from the item data and added to the UIElementManager.
     */
    public static SearchedItemPagination create(ItemHashMap itemHashMap, GeneralManager generalManager) {
        //Get keys, pull the strings out, set to list
        Set<String> itemIDs = itemHashMap.keySet().stream()
            .map(IdHashKey::getValue)
            .collect(Collectors.toSet());
        SearchedItemPagination pagination = new SearchedItemPagination(itemHashMap);
        pagination.updateContent(itemIDs);
        UIUtils.setSize(pagination, UISizeControl.SEARCHED_ITEM_PAGINATION_WIDTH.getValue(), UISizeControl.SEARCHED_ITEM_PAGINATION_HEIGHT.getValue());
        generalManager.addUIElement(UIElementName.SEARCHED_ITEM_PAGINATION, pagination);
        return pagination;
    }

    /**
     * Updates the pagination content based on the provided set of item IDs.
     * This method should be called whenever the search results change to refresh the displayed items.
     * 
     * @pre itemIDs should be a set of valid item IDs corresponding to the current search results.
     *      itemHashMap should contain the corresponding item data for those IDs.
     * 
     * @param itemIDs A set of item IDs corresponding to the current search results to update the pagination content with.
     */
    public void updateContent(Set<String> itemIDs) {
        if( ! itemIDs.isEmpty() ) {
            updatePageCount(itemIDs.size());
            setPageFactory(pageIndex -> createPageContent(pageIndex, itemIDs));
        }else{
            setPageCount(1);
            setPageFactory(pageIndex -> EMPTY_LABEL);   
        }
    }
    
    /**
     * Creates page content with optimized row access for dynamic content.
     * Always creates fresh content to reflect runtime changes.
     * 
     * @pre pageIndex should be a valid index corresponding to the current page count.
     *      itemIDs should be a set of valid item IDs corresponding to the current search
     *      results, with the itemHashMap containing the corresponding item data for those IDs.
     * 
     * @param pageIndex The index of the page to create content for, used to determine which items to display on that page.
     * @param itemIDs A set of item IDs corresponding to the current search results, used to determine which items to display on the page.
     * @return A container with the item panels for the items to be displayed on the page,
     *  created based on the provided item IDs and their corresponding data in the itemHashMap.
     */
    SearchedItemContainer createPageContent(int pageIndex, Set<String> itemIDs) {
        SearchedItemContainer container = new SearchedItemContainer();
        List<String> itemIDList = new ArrayList<>(itemIDs); // Convert set to list for indexed access
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, itemIDs.size());
        
        for( int i = fromIndex; i < toIndex; i++ ) {
            itemHashMap.getItem(itemIDList.get(i)).ifPresent(item -> {
                SearchedItemPanel itemPanel = new SearchedItemPanel(item);
                container.addItemPanel(itemPanel);
            });
        }
        
        return container;
    }
    
    /**
     * Updates the page count when table content changes.
     * Call this method after modifying the table data.
     * @param totalItems The current total number of items in the search results, used to calculate the new page count for the pagination component.
     */
    public void updatePageCount(int totalItems) {
        int newPageCount = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
        setPageCount(Math.max(1, newPageCount)); // Ensure at least 1 page
    }

}
