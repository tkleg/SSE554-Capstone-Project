package org.troy.capstone.managers;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.troy.capstone.constants.UIDataName;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.search_engine.SearchEngine;

import javafx.scene.Node;
import javafx.scene.control.Button;
import tech.tablesaw.api.Table;

public class GeneralManager {
    private final UIElementManager uiManager;
    private final SearchEngine searchEngine;

    public GeneralManager(Table table) {
        uiManager = new UIElementManager();
        searchEngine = new SearchEngine(table);
    }

    public Optional<Node> getUIElement(UIElementName key) {
        return uiManager.getElement(key);
    }

    /**
     * Gets the search data from the UIElementManager.
     * 
     * pre-conditions: None, error handling is done within the UIElementManager
      *
      * @return Map<UIDataName, Object> : The search data containing the filters to be applied
     */
    public Map<UIDataName, Object> getSearchData() {
        return uiManager.getSearchData();
    }

    /**
     * Adds a UI element to the UIElementManager
     * 
     * pre-conditions: key and element are not null
     *
     * @param key (UIElementName) : The key representing the UI element
     * @param element (Node) : The UI element to be added
     */
    public void addUIElement(UIElementName key, Node element) {
        uiManager.addElement(key, element);
    }

    public void setButton(Button button) {
        uiManager.setButton(button);
        button.setOnAction(e -> filterAndPrintNumberOfResults());
    }

    public Button getButton() {
        return uiManager.getButton();
    }

    /**
     * Gets a UI element from the UIElementManager, filters data, and updates the UI with the filtered results.
     * 
     * pre-conditions: None, error handling is done within the SearchEngine and UIElementManager
     * 
     * post-conditions: The UI is updated with the filtered results based on the current search data from the UIElementManager
     */
    public void filterAndPrintNumberOfResults() {
        Map<UIDataName, Object> searchData = getSearchData();
        System.out.println("Search Data: " + searchData);
        Set<String> filteredIDs = searchEngine.filterItems(searchData);
        uiManager.updateSearchedItemPagination(filteredIDs);
    }

}
