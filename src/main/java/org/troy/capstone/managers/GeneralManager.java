package org.troy.capstone.managers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.constants.UIDataName;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.interfaces.SearchedItemPanelDestinationUI;
import org.troy.capstone.interfaces.SearchedItemPanelSourceUI;
import org.troy.capstone.search_engine.SearchEngine;
import org.troy.capstone.search_engine.sorting.Sorter;

import javafx.scene.Node;
import javafx.scene.control.Button;
import tech.tablesaw.api.Table;

/**
 * The GeneralManager class is responsible for managing the UI elements, the search engine, and the recently viewed content.
 * It provides methods to interact with the UI elements, retrieve search data, and perform search operations.
 */
public class GeneralManager {
    /** The UIElementManager instance for managing UI elements. */
    private final UIElementManager uiManager;

    /** The SearchEngine instance for performing search operations. */
    private final SearchEngine searchEngine;

    /** The ItemHashMap containing all items, used by the SearchEngine for filtering and searching and by the RecentlyViewedManager for retrieving items. */
    private final ItemHashMap itemHashMap;

    /** The original table containing the items, used for retrieving items by index. */
    private final Table table;

    /** A flag to track whether the RecentlyViewedManager has been created. Used to prevent duplicate creation. */
    private boolean recentlyViewedManagerCreated = false;

    /** A flag to track whether the SimilarItemsManager has been created. Used to prevent duplicate creation. */
    private boolean similarItemsManagerCreated = false;

    /** Constructor for GeneralManager, filled from a tablesaw Table.
     * @param table The tablesaw Table containing the item data to be used by the SearchEngine.
     * @param itemHashMap The ItemHashMap containing all items, used by the SearchEngine for filtering and searching and by the RecentlyViewedManager for retrieving items.
     */
    public GeneralManager(Table table, ItemHashMap itemHashMap) {
        this.itemHashMap = itemHashMap;
        this.table = table;
        uiManager = new UIElementManager();
        searchEngine = new SearchEngine(table);
    }

    /**
     * Gets a UI element from the UIElementManager based on the provided key.
     * 
     * @pre key is not null.
      *
      * @param key The key representing the UI element to retrieve.
      * @return An Optional containing the UI element if found, or an empty Optional if not found.
     */
    public Optional<Node> getUIElement(UIElementName key) {
        return uiManager.getElement(key);
    }

    /**
     * Gets the search data from the UIElementManager.
     * 
     * @pre None, error handling is done within the UIElementManager.
      *
      * @return The search data containing the filters to be applied.
     */
    public Map<UIDataName, Object> getSearchData() {
        return uiManager.getSearchData();
    }

    /**
     * Adds a UI element to the UIElementManager, and creates the RecentlyViewedManager if the necessary UI elements are present and it has not already been created.
     * 
     * @pre key and element are not null.
     * @post The UI element is added to the UIElementManager and can be retrieved using the provided key. If the necessary UI elements are present and the RecentlyViewedManager has not already been created, it is created.
     * @param key The key representing the UI element.
     * @param element The UI element to be added.
     */
    public void addUIElement(UIElementName key, Node element) {
        uiManager.addElement(key, element);
        if( !recentlyViewedManagerCreated && readyToMakeRecentlyViewedManager()) {
            RecentlyViewedManager.create(itemHashMap, (SearchedItemPanelDestinationUI) uiManager.getElement(UIElementName.RECENTLY_VIEWED_WINDOW).get(), (SearchedItemPanelSourceUI) uiManager.getElement(UIElementName.SEARCHED_ITEM_PAGINATION).get());
            recentlyViewedManagerCreated = true;
        }
        if( !similarItemsManagerCreated && readyToMakeSimilarItemsManager()) {
            SimilarItemsManager.create(itemHashMap, table, (SearchedItemPanelDestinationUI) uiManager.getElement(UIElementName.SIMILAR_ITEMS_CONTAINER).get(), (SearchedItemPanelSourceUI) uiManager.getElement(UIElementName.SEARCHED_ITEM_PAGINATION).get());
            similarItemsManagerCreated = true;
        }
    }

    /**
     * Checks if the necessary UI elements for creating the RecentlyViewedManager (SearchedItemPagination and RecentlyViewedWindow) are present in the UIElementManager.
     * 
     * @return true if both the RecentlyViewedWindow and SearchedItemPagination components are present in the UIElementManager, false otherwise.
     */
    private boolean readyToMakeRecentlyViewedManager() {
        return uiManager.getElement(UIElementName.RECENTLY_VIEWED_WINDOW).isPresent() && uiManager.getElement(UIElementName.SEARCHED_ITEM_PAGINATION).isPresent();
    }

    /**
     * Checks if the necessary UI elements for creating the SimilarItemsManager (SearchedItemPagination and SimilarItemsContainer) are present in the UIElementManager.
     * 
     * @return true if both the SimilarItemsContainer and SearchedItemPagination components are present in the UIElementManager, false otherwise.
     */
    private boolean readyToMakeSimilarItemsManager() {
        return uiManager.getElement(UIElementName.SIMILAR_ITEMS_CONTAINER).isPresent() && uiManager.getElement(UIElementName.SEARCHED_ITEM_PAGINATION).isPresent();
    }

    /**
     * Sets the button in the UIElementManager and assigns an action to it that filters the search results and updates the UI when clicked.
     * 
     * @pre button is not null.
     * @post The button is set in the UIElementManager and its action is assigned to filter and update the UI when clicked.
     * @param button The Button to be set in the UIElementManager
     */
    public void setButton(Button button) {
        uiManager.setButton(button);
        button.setOnAction(e -> filterAndPrintNumberOfResults());
    }

    /**
     * Gets the button from the UIElementManager.
     *
     * @return The Button from the UIElementManager
     */
    public Button getButton() {
        return uiManager.getButton();
    }

    /**
     * Gets a UI element from the UIElementManager, filters data, sorts it, and updates the UI with the filtered results.
     * 
     * @pre None, error handling is done within the SearchEngine and UIElementManager.
     * 
     * @post The UI is updated with the filtered and sorted results based on the current search data from the UIElementManager.
     */
    public void filterAndPrintNumberOfResults() {
        Map<UIDataName, Object> searchData = getSearchData();
        System.out.println("Search Data: " + searchData);
        Table filteredTable = searchEngine.filterItems(searchData);
        Table sortedTable = Sorter.sortTable(filteredTable, searchData.get(UIDataName.SORTING_OPTION));
        List<String> sortedAndFilteredItemIds = sortedTable.stringColumn(TableColumnName.ID.getColumnName()).asList();
        uiManager.updateSearchedItemPagination( sortedAndFilteredItemIds );
    }

}