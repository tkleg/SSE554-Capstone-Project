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

public class SearchedItemPagination extends Pagination {
    private static final int ITEMS_PER_PAGE = 10;
    
    private final ItemHashMap itemHashMap;

    private static final Label EMPTY_LABEL = new Label("No items found.");

    private SearchedItemPagination(ItemHashMap itemHashMap) {
        this.itemHashMap = itemHashMap;
    }

    public static int getItemsPerPage() {
        return ITEMS_PER_PAGE;
    }

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
     */
    private SearchedItemContainer createPageContent(int pageIndex, Set<String> itemIDs) {
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
     */
    public void updatePageCount(int totalItems) {
        int newPageCount = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);
        setPageCount(Math.max(1, newPageCount)); // Ensure at least 1 page
    }
    
    /**
     * Refreshes the current page to reflect data changes.
     * Useful after runtime modifications to display updated content.
     */
    /*public void refreshCurrentPage() {
        int currentPage = getCurrentPageIndex();
        // Force recreation by setting page factory again
        setPageFactory(pageIndex -> createPageContent(pageIndex, List.copyOf(itemHashMap.keySet())));
        setCurrentPageIndex(currentPage);
    }*/

}
