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

/**
 * The GeneralManager class is responsible for managing the UI elements and the search engine.
 * It provides methods to interact with the UI elements, retrieve search data, and perform search operations.
 */
public class GeneralManager {
    /** The UIElementManager instance for managing UI elements */
    private final UIElementManager uiManager;
    /** The SearchEngine instance for performing search operations */
    private final SearchEngine searchEngine;

    /** Constructor for GeneralManager
     * @param table The tablesaw Table containing the item data to be used by the SearchEngine
     */
    public GeneralManager(Table table) {
        uiManager = new UIElementManager();
        searchEngine = new SearchEngine(table);
    }

    /**
     * Gets a UI element from the UIElementManager based on the provided key.
     * 
     * @pre <ul><li>key is not null.</li></ul>
      *
      * @param key The key representing the UI element to retrieve
      * @return An Optional containing the UI element if found, or an empty Optional if not found
     */
    public Optional<Node> getUIElement(UIElementName key) {
        return uiManager.getElement(key);
    }

    /**
     * Gets the search data from the UIElementManager.
     * 
     * @pre <ul><li>None, error handling is done within the UIElementManager.</li></ul>
      *
      * @return The search data containing the filters to be applied
     */
    public Map<UIDataName, Object> getSearchData() {
        return uiManager.getSearchData();
    }

    /**
     * Adds a UI element to the UIElementManager
     * 
     * @pre <ul><li>key and element are not null.</li></ul>
     *
     * @param key The key representing the UI element
     * @param element The UI element to be added
     */
    public void addUIElement(UIElementName key, Node element) {
        uiManager.addElement(key, element);
    }

    /**
     * Sets the button in the UIElementManager and assigns an action to it that filters the search results and updates the UI when clicked.
     * 
     * @pre <ul><li>button is not null.</li></ul>
     *
     * @param button The Button to be set in the UIElementManager
     */
    public void setButton(Button button) {
        uiManager.setButton(button);
        button.setOnAction(e -> filterAndPrintNumberOfResults());
    }

    /**
     * Gets the button from the UIElementManager.
     * 
     * @pre <ul><li>None, error handling is done within the UIElementManager.</li></ul>
     *
     * @return The Button from the UIElementManager
     */
    public Button getButton() {
        return uiManager.getButton();
    }

    /**
     * Gets a UI element from the UIElementManager, filters data, and updates the UI with the filtered results.
     * 
     * @pre <ul><li>None, error handling is done within the SearchEngine and UIElementManager.</li></ul>
     * 
     * @post <ul><li>The UI is updated with the filtered results based on the current search data from the UIElementManager.</li></ul>
     */
    public void filterAndPrintNumberOfResults() {
        Map<UIDataName, Object> searchData = getSearchData();
        System.out.println("Search Data: " + searchData);
        Set<String> filteredIDs = searchEngine.filterItems(searchData);
        uiManager.updateSearchedItemPagination(filteredIDs);
    }

}
