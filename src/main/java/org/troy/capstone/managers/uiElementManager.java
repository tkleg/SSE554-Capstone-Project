package org.troy.capstone.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.troy.capstone.constants.UIDataName;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.ui_components.filters.categorical.FiltersContainer;
import org.troy.capstone.ui_components.filters.stars.StarRatingFilter;
import org.troy.capstone.ui_components.items.searched.SearchedItemPagination;

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

    /**
     * Gathers current values of UI elements and returns them in a map for use
     * in search queries. Logs any missing elements or type errors but continues gathering other data.
     * 
     * pre-conditions: UI elements should be added to the manager with the expected keys and types before this method is called.
     * 
     * @return searchData (Map<UIDataName, Object>): a map containing the current values of UI elements for use in search queries
     */
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

    /**
     * Updates the searched item pagination component with new search results.
     * Logs missing pagination component or type error with the component.
     * 
     * pre-conditions: a searched item pagination component should be added to the manager with the expected key and type before this method is called.
     *  itemIDs should be a set of valid item IDs corresponding to search results.
     * 
     * @param itemIDs (Set<String>): a set of item IDs corresponding to search results to update the pagination component with.
     */
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
