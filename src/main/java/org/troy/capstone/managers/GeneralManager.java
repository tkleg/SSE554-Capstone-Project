package org.troy.capstone.managers;

import java.util.Map;
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

    public UIElementManager getUiManager() {
        return uiManager;
    }

    public Map<UIDataName, Object> getSearchData() {
        return uiManager.getSearchData();
    }

    public void addUIElement(UIElementName key, Node element) {
        switch(key){
            case SEARCH_BUTTON -> ((Button) element).setOnAction(e -> filterAndPrintNumberOfResults());
            default -> uiManager.addElement(key, element);
        }
    }

    public void filterAndPrintNumberOfResults() {
        Map<UIDataName, Object> searchData = getSearchData();
        System.out.println("Search Data: " + searchData);
        Set<String> filteredIDs = searchEngine.filterItems(searchData);
        uiManager.updateSearchedItemPagination(filteredIDs);
    }

}
