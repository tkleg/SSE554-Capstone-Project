package org.troy.capstone.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.troy.capstone.constants.uiDataNames;
import org.troy.capstone.constants.uiElementName;
import org.troy.capstone.uiMock.FiltersContainer;

import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class uiElementManager {
    private Map<String, Node> uiElements;
    
    public uiElementManager() {
        uiElements = new HashMap<>();
    }

    public Optional<Node> getElement(uiElementName key) {
        return Optional.ofNullable(uiElements.get(key.getValue()));
    }

    public void addElement(uiElementName key, Node element) {
        uiElements.put(key.getValue(), element);
    }

    public Set<String> getAllKeys() {
        return uiElements.keySet();
    }

    public Map<uiDataNames, Object> getSearchData(){
        Map<uiDataNames, Object> searchData = new HashMap<>();
        
        Optional<Node> minPriceSliderOpt = getElement(uiElementName.MIN_PRICE_SLIDER);
        if( minPriceSliderOpt.isPresent() )
            searchData.put(uiDataNames.MIN_PRICE, ((Slider)minPriceSliderOpt.get()).getValue());

        Optional<Node> maxPriceSliderOpt = getElement(uiElementName.MAX_PRICE_SLIDER);
        if( maxPriceSliderOpt.isPresent() )
            searchData.put(uiDataNames.MAX_PRICE, ((Slider)maxPriceSliderOpt.get()).getValue());

        Optional<Node> searchFieldNodeOpt = getElement(uiElementName.SEARCH_FIELD);
        if( searchFieldNodeOpt.isPresent() )
            searchData.put(uiDataNames.SEARCH_QUERY, ((TextField)searchFieldNodeOpt.get()).getText() );

        Optional<Node> filtersContainerOpt = getElement(uiElementName.FILTERS_CONTAINER);
        if( filtersContainerOpt.isPresent() )
            searchData.put(uiDataNames.FILTERS_CONTAINER, ((FiltersContainer)filtersContainerOpt.get()).getSelectedFilters());
        
        return searchData;
    }
    
}
