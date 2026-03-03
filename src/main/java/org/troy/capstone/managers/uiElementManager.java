package org.troy.capstone.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.troy.capstone.constants.uiDataNames;
import org.troy.capstone.constants.uiElementName;
import org.troy.capstone.uiComponents.filters.categorical.FiltersContainer;
import org.troy.capstone.uiComponents.filters.stars.StarRatingFilter;
import org.troy.capstone.uiComponents.items.searched.SearchedItemPagination;

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
        .ifPresentOrElse( e -> searchData.put(uiDataNames.MIN_PRICE, ((Slider)e).getValue()),
        () -> System.out.println("Min price slider not found in UIElementManager, cannot include min price in search data.") );

        getElement(uiElementName.MAX_PRICE_SLIDER)
        .ifPresentOrElse( e -> searchData.put(uiDataNames.MAX_PRICE, ((Slider)e).getValue()),
        () -> System.out.println("Max price slider not found in UIElementManager, cannot include max price in search data.") );

        getElement(uiElementName.SEARCH_FIELD)
        .ifPresentOrElse( e -> searchData.put(uiDataNames.SEARCH_QUERY, ((TextField)e).getText()),
        () -> System.out.println("Search field not found in UIElementManager, cannot include search query in search data.") );
        
        getElement(uiElementName.FILTERS_CONTAINER)
        .ifPresentOrElse( e -> searchData.put(uiDataNames.FILTERS_CONTAINER, ((FiltersContainer)e).getSelectedFilters()),
        () -> System.out.println("Filters container not found in UIElementManager, cannot include filters in search data.") );
        
        getElement(uiElementName.STAR_RATING_FILTER)
        .ifPresentOrElse( e -> searchData.put(uiDataNames.MIN_STAR_RATING, ((StarRatingFilter)e).getSelectedRating()),
        () -> System.out.println("Star rating filter not found in UIElementManager, cannot include star rating in search data.") );

        return searchData;
    }

    public void updateSearchedItemPagination(Set<String> itemIDs) {
        getElement(uiElementName.SEARCHED_ITEM_PAGINATION)
        .ifPresentOrElse( e -> ((SearchedItemPagination)e).updateContent(itemIDs),
        () -> System.out.println("Searched item pagination not found in UIElementManager, cannot update search results.") );
    }
    
}
