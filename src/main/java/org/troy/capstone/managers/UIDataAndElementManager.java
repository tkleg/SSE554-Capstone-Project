package org.troy.capstone.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.troy.capstone.constants.UIDataName;
import org.troy.capstone.search_engine.sorting.RowComparator;

import javafx.scene.control.Button;

/**
 * The {@code UIElementManager} class is responsible for managing the UI elements in the application.
 * It provides methods to add and retrieve UI elements, gather search data from the UI, and update the UI with search results.
 */
public class UIDataAndElementManager {
    /** A map of UI element names to their corresponding nodes */
    //private final Map<UIElementName, Node> uiElements;

    /** A map of UI data names to their supplier functions. */
    private final Map<UIDataName, Supplier<Object>> uiDataSuppliers;

    /** The search button in the UI */
    private Button searchButton;
    
    /** Constructor for {@code UIElementManager}, initializes the map for storing UI elements */
    public UIDataAndElementManager() {
        //uiElements = new HashMap<>();
        uiDataSuppliers = new HashMap<>();
    }

    /** Retrieves a UI element based on the provided key.
     * Logs a message if the element is not found in the manager.
     * 
     * @pre key is not null.
     *
     * @param key The {@code UIElementName} representing the UI element to retrieve
     * @return An {@code Optional} containing the UI element if found, or an empty {@code Optional} if not found
     */
    /*public Optional<Node> getElement(UIElementName key) {
        if (!uiElements.containsKey(key))
            System.out.println("UI element with key " + key + " not found in UIElementManager.");
        return Optional.ofNullable(uiElements.get(key));
    }*/

    /** Adds a UI element to the manager with the specified key.
     * Logs a message if the key or element is null.
     * 
     * @pre key and element are not null.
     *
     * @param key The key representing the UI element
     * @param element The UI element to be added
     */
    /*(public void addElement(UIElementName key, Node element) {
        uiElements.put(key, element);
    }*/

    /** Gets the search button from the manager.
     * 
     * @pre {@code searchButton} is not null.
     *
     * @return The {@code Button} that was set in the manager
     */
    public Button getButton() {
        return searchButton;
    }

    /** Sets the search button in the manager and assigns an action to it that filters the search results and updates the UI when clicked.
     * Logs a message if the button is null.
     * 
     *
     * @param button The {@code Button} to be set in the manager
     */
    /*public void setButton(Button button) {
        this.searchButton = button;
    }*/

    /**
     * Adds a {@code Supplier} for UI data to the manager with the specified key.
     * 
     * @pre key and supplier are not null.
     * @post The supplier is added to the manager and can be retrieved using the provided key.
     * 
     * @param key The {@code UIDataName} representing the type of data the supplier provides.
     * @param supplier The {@code Supplier} that provides the UI data when called.
     */
    public void addUIDataSupplier(UIDataName key, Supplier<Object> supplier) {
        uiDataSuppliers.put(key, supplier);
    }
    

    public Optional<Object> getUIData(UIDataName key) {
        return Optional.ofNullable(uiDataSuppliers.get(key)).map(Supplier::get);
    }

    /**
     * Gathers current values of data from suppliers and returns them in a map for use
     * in search queries. Logs any missing elements or type errors but continues gathering other data.
     * 
     * @pre data suppliers should be added to the manager with the expected keys and types before this method is called.
     * 
     * @return A map containing the current values of UI data for use in search queries
     */
    public Map<UIDataName, Object> getSearchData(){
        Map<UIDataName, Object> searchData = new HashMap<>();
        
        try{
            //getElement(UIElementName.MIN_PRICE_SLIDER)
            //.ifPresentOrElse( e -> searchData.put(UIDataName.MIN_PRICE, (float) Math.round(((Slider)e).getValue())),
            //() -> System.out.println("Min price slider not found in UIElementManager, cannot include min price in search data.") );
            getUIData(UIDataName.MIN_PRICE)
            .ifPresentOrElse( e -> searchData.put(UIDataName.MIN_PRICE, (float) Math.round((double)e)),
            () -> System.out.println("getMin not found in UIElementManager, cannot include min price in search data.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving min price slider value: " + ex.getMessage());
        }

        try{
            //getElement(UIElementName.MAX_PRICE_SLIDER)
            //.ifPresentOrElse( e -> searchData.put(UIDataName.MAX_PRICE, (float) Math.round(((Slider)e).getValue())),
            //() -> System.out.println("Max price slider not found in UIElementManager, cannot include max price in search data.") );
            getUIData(UIDataName.MAX_PRICE)
            .ifPresentOrElse( e -> searchData.put(UIDataName.MAX_PRICE, (float) Math.round((double)e)),
            () -> System.out.println("getMax not found in UIElementManager, cannot include max price in search data.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving max price slider value: " + ex.getMessage());
        }
        try{
            //getElement(UIElementName.SEARCH_FIELD)
            //.ifPresentOrElse( e -> searchData.put(UIDataName.SEARCH_QUERY, ((TextField)e).getText()),
            //() -> System.out.println("Search field not found in UIElementManager, cannot include search query in search data.") );
            getUIData(UIDataName.SEARCH_QUERY)
            .ifPresentOrElse( e -> searchData.put(UIDataName.SEARCH_QUERY, (String)e),
            () -> System.out.println("getSearchQuery not found in UIElementManager, cannot include search query in search data.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving search field value: " + ex.getMessage());
        }

        try{
            //getElement(UIElementName.FILTERS_CONTAINER)
            //.ifPresentOrElse( e -> searchData.put(UIDataName.FILTERS_CONTAINER, ((FiltersContainer)e).getSelectedFilters()),
            //() -> System.out.println("Filters container not found in UIElementManager, cannot include filters in search data.") );
            getUIData(UIDataName.FILTERS_CONTAINER)
            .ifPresentOrElse( e -> searchData.put(UIDataName.FILTERS_CONTAINER, (Map<String, Set<String>>)e),
            () -> System.out.println("getSelectedFilters not found in UIElementManager, cannot include filters in search data.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving filters container value: " + ex.getMessage());
        }

        try{
            //getElement(UIElementName.STAR_RATING_FILTER)
            //.ifPresentOrElse( e -> searchData.put(UIDataName.MIN_STAR_RATING, ((StarRatingFilter)e).getSelectedRating()),
            //() -> System.out.println("Star rating filter not found in UIElementManager, cannot include star rating in search data.") );
            getUIData(UIDataName.MIN_STAR_RATING)
            .ifPresentOrElse( e -> searchData.put(UIDataName.MIN_STAR_RATING, (int)e),
            () -> System.out.println("getMinStarRating not found in UIElementManager, cannot include star rating in search data.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving star rating filter value: " + ex.getMessage());
        }

        try{
            //getElement(UIElementName.SORTING_OPTION_DROPDOWN)
            //.ifPresentOrElse( e -> searchData.put(UIDataName.SORTING_OPTION, ((ComboBox<?>)e).getValue()),
            //() -> System.out.println("Sorting option dropdown not found in UIElementManager, cannot include sorting option in search data.") );
            getUIData(UIDataName.SORTING_OPTION)
            .ifPresentOrElse( e -> searchData.put(UIDataName.SORTING_OPTION, (RowComparator)e),
            () -> System.out.println("getSortingOption not found in UIElementManager, cannot include sorting option in search data.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving sorting option dropdown value: " + ex.getMessage());
        }

        return searchData;
    }

    /**
     * Updates the searched item pagination component with new search results.
     * Logs missing pagination component or type error with the component.
     * 
     * @pre A {@code SearchedItemPagination} component should be added to the manager with the expected key and type before this method is called.
     *      itemIDs should be a list of valid item IDs corresponding to search results.
     * 
     * @param itemIDs A list of item IDs corresponding to search results to update the {@code SearchedItemPagination} component with.
     */
    /*public void updateSearchedItemPagination(List<String> itemIDs) {
        try{
            getElement(UIElementName.SEARCHED_ITEM_PAGINATION)
            .ifPresentOrElse( e -> ((SearchedItemPagination)e).update(itemIDs),
            () -> System.out.println("Searched item pagination not found in UIElementManager, cannot update search results.") );
        }catch (ClassCastException ex) {
            System.out.println("Error retrieving searched item pagination value: " + ex.getMessage());
        }
    }*/

    
}
