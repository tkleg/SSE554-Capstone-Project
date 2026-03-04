package org.troy.capstone.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.troy.capstone.constants.UIDataName;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.uiComponents.filters.categorical.FiltersContainer;
import org.troy.capstone.uiComponents.filters.stars.StarRatingFilter;
import org.troy.capstone.uiComponents.items.searched.SearchedItemPagination;

import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class UIElementManager {
    private final Map<UIElementName, Node> uiElements;
    
    public UIElementManager() {
        uiElements = new HashMap<>();
    }

    public Optional<Node> getElement(UIElementName key) {
        if (!uiElements.containsKey(key))
            System.out.println("UI element with key " + key + " not found in UIElementManager.");
        return Optional.ofNullable(uiElements.get(key));
    }

    public void addElement(UIElementName key, Node element) {
        uiElements.put(key, element);
    }

    public Map<UIDataName, Object> getSearchData(){
        Map<UIDataName, Object> searchData = new HashMap<>();
        
        try{
            getElement(UIElementName.MIN_PRICE_SLIDER)
            .ifPresentOrElse( e -> searchData.put(UIDataName.MIN_PRICE, (float) ((Slider)e).getValue()),
            () -> System.out.println("Min price slider not found in UIElementManager, cannot include min price in search data.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving min price slider value: " + ex.getMessage());
        }

        try{
            getElement(UIElementName.MAX_PRICE_SLIDER)
            .ifPresentOrElse( e -> searchData.put(UIDataName.MAX_PRICE, (float) ((Slider)e).getValue()),
            () -> System.out.println("Max price slider not found in UIElementManager, cannot include max price in search data.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving max price slider value: " + ex.getMessage());
        }
        try{
            getElement(UIElementName.SEARCH_FIELD)
            .ifPresentOrElse( e -> searchData.put(UIDataName.SEARCH_QUERY, ((TextField)e).getText()),
            () -> System.out.println("Search field not found in UIElementManager, cannot include search query in search data.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving search field value: " + ex.getMessage());
        }

        try{
            getElement(UIElementName.FILTERS_CONTAINER)
            .ifPresentOrElse( e -> searchData.put(UIDataName.FILTERS_CONTAINER, ((FiltersContainer)e).getSelectedFilters()),
            () -> System.out.println("Filters container not found in UIElementManager, cannot include filters in search data.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving filters container value: " + ex.getMessage());
        }

        try{
            getElement(UIElementName.STAR_RATING_FILTER)
            .ifPresentOrElse( e -> searchData.put(UIDataName.MIN_STAR_RATING, ((StarRatingFilter)e).getSelectedRating()),
            () -> System.out.println("Star rating filter not found in UIElementManager, cannot include star rating in search data.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving star rating filter value: " + ex.getMessage());
        }

        return searchData;
    }

    public void updateSearchedItemPagination(Set<String> itemIDs) {
        try{
            getElement(UIElementName.SEARCHED_ITEM_PAGINATION)
            .ifPresentOrElse( e -> ((SearchedItemPagination)e).updateContent(itemIDs),
            () -> System.out.println("Searched item pagination not found in UIElementManager, cannot update search results.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving searched item pagination value: " + ex.getMessage());
        }
    }

    //Useful for testing to reset the UIElementManager state between tests
    public void clearElements() {
        uiElements.clear();
    }
    
}
