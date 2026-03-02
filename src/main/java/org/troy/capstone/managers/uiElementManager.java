package org.troy.capstone.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.troy.capstone.constants.uiDataNames;
import org.troy.capstone.constants.uiElementName;
import org.troy.capstone.uiComponents.filters.categorical.FiltersContainer;

import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class UIElementManager {
    private final Map<uiElementName, Node> uiElements;
    
    public UIElementManager() {
        uiElements = new HashMap<>();
    }

    public Optional<Node> getElement(uiElementName key) {
        if (!uiElements.containsKey(key))
            System.out.println("UI element with key " + key + " not found in UIElementManager.");
        return Optional.ofNullable(uiElements.get(key));
    }

    public void addElement(uiElementName key, Node element) {
        uiElements.put(key, element);
    }

    public Set<uiElementName> getAllKeys() {
        return uiElements.keySet();
    }

    public Map<uiDataNames, Object> getSearchData(){
        Map<uiDataNames, Object> searchData = new HashMap<>();
        
        getElement(uiElementName.MIN_PRICE_SLIDER)
        .ifPresent( e -> searchData.put(uiDataNames.MIN_PRICE, ((Slider)e).getValue()) );

        getElement(uiElementName.MAX_PRICE_SLIDER)
        .ifPresent( e -> searchData.put(uiDataNames.MAX_PRICE, ((Slider)e).getValue()) );

        getElement(uiElementName.SEARCH_FIELD)
        .ifPresent( e -> searchData.put(uiDataNames.SEARCH_QUERY, ((TextField)e).getText()) );

        getElement(uiElementName.FILTERS_CONTAINER)
        .ifPresent( e -> searchData.put(uiDataNames.FILTERS_CONTAINER, ((FiltersContainer)e).getSelectedFilters()) );
        
        return searchData;
    }
    
}
