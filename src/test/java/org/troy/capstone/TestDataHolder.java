package org.troy.capstone;

import java.util.List;

import org.troy.capstone.constants.DataPath;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.managers.SimilarItemsManager;
import org.troy.capstone.ui_components.items.SimilarItemsContainer;
import org.troy.capstone.ui_components.items.searched.SearchedItemPagination;
import org.troy.capstone.utils.TableUtils;

import javafx.embed.swing.JFXPanel;
import tech.tablesaw.api.Table;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class TestDataHolder {
    private static final Table table;
    private static final ItemHashMap itemHashMap;
    private static final List<String> allItemIds;
    private static final SimilarItemsContainer similarItemsContainer;
    private static final SearchedItemPagination searchedItemPagination;
    private static final SimilarItemsManager similarItemsManager;

    static {
        table = TableUtils.readData(DataPath.CLEANED_ATTRIBUTED_DATA);
        itemHashMap = ItemHashMap.fromTable(table);
        allItemIds = itemHashMap.getItemIdsAsList();
        new JFXPanel();
        similarItemsContainer = SimilarItemsContainer.create();
        searchedItemPagination = new SearchedItemPagination(itemHashMap);
        Config.graphBuildingEnabled = true;
        similarItemsManager = SimilarItemsManager.create(itemHashMap, table, similarItemsContainer, searchedItemPagination);
        Config.graphBuildingEnabled = false;
    }

    public static void main(String[] args) {
        System.out.println("Table loaded with " + table.rowCount() + " rows and " + table.columnCount() + " columns.");
        System.out.println("ItemHashMap contains " + itemHashMap.size() + " items.");
        System.out.println("First 5 item IDs: " + allItemIds.subList(0, 5));
    }

    public static Table getTableCopy() {
        return table.copy();
    }

    public static ItemHashMap getItemHashMapCopy() {
        return itemHashMap.copy();
    }

    public static List<String> getAllItemIdsCopy() {
        return List.copyOf(allItemIds);
    }

    public static SimilarItemsContainer getSimilarItemsContainer() {
        return similarItemsContainer;
    }

    public static SearchedItemPagination getSearchedItemPagination() {
        return searchedItemPagination;
    }

    public static SimilarItemsManager getSimilarItemsManager() {
        return similarItemsManager;
    }

    public static SimilarItemsManager getFreshSimilarItemsManager() {
        Table freshTable = table.copy();
        ItemHashMap freshMap = ItemHashMap.fromTable(freshTable);
        SimilarItemsContainer freshContainer = SimilarItemsContainer.create();
        SearchedItemPagination freshPagination = new SearchedItemPagination(freshMap);
        Config.graphBuildingEnabled = true;
        SimilarItemsManager freshManager = SimilarItemsManager.create(freshMap, freshTable, freshContainer, freshPagination);
        Config.graphBuildingEnabled = false;
        return freshManager;
    }
}