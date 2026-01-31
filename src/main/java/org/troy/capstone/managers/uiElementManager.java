package org.troy.capstone.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.troy.capstone.constants.uiElementNames;
import org.troy.capstone.uiMock.FiltersContainer;

import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class uiElementManager {
    private Map<String, Node> uiElements;
    
    public uiElementManager() {
        uiElements = new HashMap<>();
    }

    public Optional<Node> getElement(String key) {
        return Optional.ofNullable(uiElements.get(key));
    }

    public void addElement(String key, Node element) {
        uiElements.put(key, element);
    }

    public Set<String> getAllKeys() {
        return uiElements.keySet();
    }

    public Map<String, Object> getSearchData(){
        Map<String, Object> searchData = new HashMap<>();
        
        Optional<Node> minPriceSliderOpt = getElement(uiElementNames.MIN_PRICE_SLIDER);
        if( minPriceSliderOpt.isPresent() )
            searchData.put("MIN_PRICE", ((Slider)minPriceSliderOpt.get()).getValue());

        Optional<Node> maxPriceSliderOpt = getElement(uiElementNames.MAX_PRICE_SLIDER);
        if( maxPriceSliderOpt.isPresent() )
            searchData.put("MAX_PRICE", ((Slider)maxPriceSliderOpt.get()).getValue());

        Optional<Node> searchFieldNodeOpt = getElement(uiElementNames.SEARCH_FIELD);
        if( searchFieldNodeOpt.isPresent() )
            searchData.put("SEARCH_QUERY", ((TextField)searchFieldNodeOpt.get()).getText() );

        Optional<Node> filtersContainerOpt = getElement(uiElementNames.FILTERS_CONTAINER);
        if( filtersContainerOpt.isPresent() )
            searchData.put("FILTERS_CONTAINER", ((FiltersContainer)filtersContainerOpt.get()).getSelectedFilters());
        
        return searchData;
    }
    
}
