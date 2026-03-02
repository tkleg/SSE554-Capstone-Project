package org.troy.capstone.managers;

import java.util.Map;

import org.troy.capstone.constants.uiDataNames;
import org.troy.capstone.constants.uiElementName;
import org.troy.capstone.searchEngine.SearchEngine;
import org.troy.capstone.uiComponents.items.searched.SearchedItemPagination;

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

    public Map<uiDataNames, Object> getSearchData() {
        return uiManager.getSearchData();
    }

    public void addUIElement(uiElementName key, Node element) {
        switch(key){
            case SEARCH_BUTTON -> ((Button) element).setOnAction(e -> filterAndPrintNumberOfResults());
            case SEARCHED_ITEM_PAGINATION -> searchEngine.setSearchedItemPagination((SearchedItemPagination) element);
            default -> uiManager.addElement(key, element);
        }
    }

    public void filterAndPrintNumberOfResults() {
        Map<uiDataNames, Object> searchData = getSearchData();
        System.out.println("Search Data: " + searchData);
        searchEngine.resetItems(searchData);
    }

}
