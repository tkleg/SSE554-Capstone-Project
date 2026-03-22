package org.troy.capstone.ui_components.search_bar;

import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.managers.GeneralManager;
import org.troy.capstone.search_engine.sorting.Comparators;
import org.troy.capstone.utils.UIUtils;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The SearchBar class represents a UI component that provides a text field for users to enter search queries and a button to search.
 */
public class SearchBar extends VBox {
    /** The text field for users to enter their search queries. */
    private final TextField searchField;
    /** The button that users click to initiate the search based on the entered query. */
    private final Button searchButton;
    /** The dropdown for users to select the sorting option for search results. */
    private final ComboBox<String> sortingOptionDropdown;
    
    /** Factory method to create a SearchBar with the appropriate size and add it to the UIElementManager.
     * @pre The SearchBar should be properly initialized to allow for user interaction with the search field and button.
     *      generalManager should be properly initialized to allow for adding the created SearchBar to it.
     * 
     * @param generalManager The GeneralManager to register the SearchBar with.
     * @return The created SearchBar instance with the search field and button initialized and added to the UIElementManager.
     */
    public static SearchBar create( GeneralManager generalManager ) {
        SearchBar searchBar = new SearchBar(generalManager);
        UIUtils.setSize(searchBar, UISizeControl.SEARCH_BAR_WIDTH.getValue(), UISizeControl.SEARCH_BAR_HEIGHT.getValue());
        return searchBar;
    }

    /**
     * Constructor for SearchBar. Initializes the search field and button, adds them to the HBox, and registers them with the GeneralManager.
     * @pre The SearchBar should be properly initialized to allow for user interaction with the search field and button and the
     *      generalManager should be properly initialized to allow for adding the created SearchBar to it.
     * 
     * @param generalManager The GeneralManager to register the SearchBar with.
     */
    public SearchBar( GeneralManager generalManager ) {

        HBox topRowBox = new HBox();
        topRowBox.setSpacing(UISizeControl.WIDTH_PADDING.getValue());
        topRowBox.setAlignment(Pos.CENTER_LEFT);
        searchField = new TextField();
        searchField.setPromptText("Enter Query Here");
        generalManager.addUIElement(UIElementName.SEARCH_FIELD, searchField);

        searchButton = new Button("Search");
        generalManager.setButton(searchButton);

        topRowBox.getChildren().addAll(searchField, searchButton);

        HBox bottomBox = new HBox();
        bottomBox.setSpacing(UISizeControl.WIDTH_PADDING.getValue());
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        Label sortByLabel = new Label("Sort By:");

        sortingOptionDropdown = new ComboBox<>();
        sortingOptionDropdown.getItems().addAll(Comparators.getComparatorNames());
        sortingOptionDropdown.setPromptText("Sort By");
        sortingOptionDropdown.getSelectionModel().selectFirst();
        sortingOptionDropdown.setEditable(false);
        sortingOptionDropdown.setOnAction(e -> {
            System.out.println("Selected sorting option: " + sortingOptionDropdown.getValue());
        });
        generalManager.addUIElement(UIElementName.SORTING_OPTION_DROPDOWN, sortingOptionDropdown);
        
        bottomBox.getChildren().addAll(sortByLabel, sortingOptionDropdown);

        this.getChildren().addAll(topRowBox, bottomBox);
        setSpacing(UISizeControl.HEIGHT_PADDING.getValue());
    }

    /**
     * Returns the text field for entering search queries.
     * @return The text field for entering search queries.
     */
    public TextField getSearchField() {
        return searchField;
    }

    /**
     * Returns the button for initiating the search.
     * @return The button for initiating the search.
     */
    public Button getSearchButton() {
        return searchButton;
    }

}
