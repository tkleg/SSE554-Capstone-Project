package org.troy.capstone.uiComponents.items.searched;

import java.util.List;

import org.troy.capstone.data_structures.ItemTable.ItemHashMap;

import javafx.scene.control.Pagination;
import tech.tablesaw.api.Table;

public class SearchedItemPagination extends Pagination {
    private static final int ITEMS_PER_PAGE = 10;
    
    private final Table table;
    private final ItemHashMap itemHashMap;

    private SearchedItemPagination(Table table, ItemHashMap itemHashMap) {
        this.table = table;
        this.itemHashMap = itemHashMap;
    }

    public static int getItemsPerPage() {
        return ITEMS_PER_PAGE;
    }

    public static SearchedItemPagination create(Table table, ItemHashMap itemHashMap) {
        SearchedItemPagination pagination = new SearchedItemPagination(table, itemHashMap);
        pagination.updatePageCount();
        pagination.setPageFactory(pagination::createPageContent);
        return pagination;
    }
    
    /**
     * Creates page content with optimized row access for dynamic content.
     * Always creates fresh content to reflect runtime changes.
     */
    private SearchedItemContainer createPageContent(Integer pageIndex) {
        SearchedItemContainer container = new SearchedItemContainer();
        
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, table.rowCount());
        
        for( int i = fromIndex; i < toIndex; i++ ) {
            itemHashMap.getItem(table.row(i).getString("ID")).ifPresent(item -> {
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
    public void updatePageCount() {
        int newPageCount = (int) Math.ceil((double) table.rowCount() / ITEMS_PER_PAGE);
        setPageCount(Math.max(1, newPageCount)); // Ensure at least 1 page
    }
    
    /**
     * Refreshes the current page to reflect data changes.
     * Useful after runtime modifications to display updated content.
     */
    public void refreshCurrentPage() {
        int currentPage = getCurrentPageIndex();
        // Force recreation by setting page factory again
        setPageFactory(this::createPageContent);
        setCurrentPageIndex(currentPage);
    }
    
    /**
     * Updates the pagination content to show only items with the specified IDs.
     * This is a simple one-call method to replace all displayed content.
     * 
     * @param itemIds Set of item IDs to display
     */
    public void updateContent(List<String> itemIds) {
        
        // Update the page factory to use filtered data
        setPageFactory(pageIndex -> {
            SearchedItemContainer container = new SearchedItemContainer();
            
            int fromIndex = pageIndex * ITEMS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, itemIds.size());
            
            for( int i = fromIndex; i < toIndex; i++ ) {
                itemHashMap.getItem(itemIds.get(i)).ifPresent(item -> {
                    SearchedItemPanel itemPanel = new SearchedItemPanel(item);
                    container.addItemPanel(itemPanel);
                });
            }
            
            return container;
        });
        
        // Update page count and refresh
        int newPageCount = (int) Math.ceil((double) itemIds.size() / ITEMS_PER_PAGE);
        setPageCount(Math.max(1, newPageCount));
        setCurrentPageIndex(0); // Reset to first page
    }
    
    /**
     * Gets the underlying table for external modifications.
     * Remember to call updatePageCount() after modifying the table.
     */
    public Table getTable() {
        return table;
    }
}
