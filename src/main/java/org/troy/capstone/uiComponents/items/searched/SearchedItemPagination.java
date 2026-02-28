package org.troy.capstone.uiComponents.items.searched;

import org.troy.capstone.data_structures.ItemTable.ItemHashMap;

import javafx.scene.control.Pagination;
import tech.tablesaw.api.Table;

public class SearchedItemPagination extends Pagination {
    private static final int ITEMS_PER_PAGE = 10;

    public static int getItemsPerPage() {
        return ITEMS_PER_PAGE;
    }

    public static SearchedItemPagination create(Table table, ItemHashMap itemHashMap) {
        int pageCount = (int) Math.ceil((double) table.rowCount() / ITEMS_PER_PAGE);
        SearchedItemPagination pagination = new SearchedItemPagination();
        pagination.setPageCount(pageCount);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * ITEMS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, table.rowCount());
            return SearchedItemContainer.createFilledContainer(table.inRange(fromIndex, toIndex), itemHashMap);
        });
        return pagination;
    }
}
