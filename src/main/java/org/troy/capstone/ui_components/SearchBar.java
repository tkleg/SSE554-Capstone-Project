package org.troy.capstone.ui_components;

import org.troy.capstone.constants.TestFXId;
import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.search_engine.sorting.comparator.RowComparator;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The {@code SearchBar} class represents a UI component that provides a text field for users to enter search queries and a button to search.
 */
public class SearchBar extends VBox {
    /** The text field for users to enter their search queries. */
    private final TextField searchField;
    /** The button that users click to initiate the search based on the entered query. */
    private final Button searchButton;
    /** The dropdown for users to select the sorting option for search results. */
    private final ComboBox<RowComparator> sortingOptionDropdown;

    /**
     * Constructor for {@code SearchBar}. Initializes the search field and button, adds them to the {@code HBox}.
     * @pre The {@code SearchBar} should be properly initialized to allow for user interaction with the search field and button.
     * 
     */
    public SearchBar() {

        HBox topRowBox = new HBox();
        topRowBox.setSpacing(UISizeControl.WIDTH_PADDING.getValue());
        topRowBox.setAlignment(Pos.CENTER_LEFT);
        searchField = new TextField();
        searchField.setPromptText("Enter Query Here");

        searchButton = new Button("Search");

        topRowBox.getChildren().addAll(searchField, searchButton);

        HBox bottomBox = new HBox();
        bottomBox.setSpacing(UISizeControl.WIDTH_PADDING.getValue());
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        Label sortByLabel = new Label("Sort By:");

        sortingOptionDropdown = new ComboBox<>();
        buildSortingOptionDropdown(bottomBox, sortByLabel);

        this.getChildren().addAll(topRowBox, bottomBox);
        setSpacing(UISizeControl.HEIGHT_PADDING.getValue());
        
    }

    /** Builds the dropdown for the Sort By selections.
     * 
     * @pre The {@code sortingOptionDropdown} should be properly initialized to allow for adding sorting options to it.
     * @post The {@code sortingOptionDropdown} is built with the appropriate sorting options, callbacks are set for displaying the options and printing the selected option, and the dropdown is added to the provided {@code bottomBox}.
     * @param bottomBox The {@code HBox} to add the {@code sortingOptionDropdown} to.
     * @param sortByLabel The {@code Label} to associate with the {@code sortingOptionDropdown} in the UI.
     */
    private void buildSortingOptionDropdown(HBox bottomBox, Label sortByLabel) {
        sortingOptionDropdown.getItems().addAll(RowComparator.getComparators());
        sortingOptionDropdown.setPromptText("Sort By");
        sortingOptionDropdown.getSelectionModel().selectFirst();
        sortingOptionDropdown.setEditable(false);

        setSortingOptionCallbacks();

        sortingOptionDropdown.setId(TestFXId.SORT_OPTION_DROPDOWN.getId());
        
        bottomBox.getChildren().addAll(sortByLabel, sortingOptionDropdown);
    }

    /**
     * Sets the callbacks for displaying a cell, selected item, and printing when an item is selected.
     * @pre The {@code sortingOptionDropdown} should be properly initialized with the expected items for the callbacks to function correctly.
     * @post The callbacks for displaying a cell, selected item, and printing when an item is selected are set for the {@code sortingOptionDropdown}.
     */
    private void setSortingOptionCallbacks() {
         //Display clean name when open
        sortingOptionDropdown.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(RowComparator item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
            }
        });
        //Display clean name when closed
        sortingOptionDropdown.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(RowComparator item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
            }
        });
        //Print selected option when changed
        sortingOptionDropdown.setOnAction(e -> {
            RowComparator selectedComparator = sortingOptionDropdown.getValue();
            System.out.println("Selected sorting option: " + (selectedComparator == null ? "None" : selectedComparator.toString()));
        });
    }

    /**
     * Returns the {@code TextField} for entering search queries.
     * @return The {@code TextField} for entering search queries.
     */
    public TextField getSearchField() {
        return searchField;
    }

    /**
     * Returns the {@code Button} for initiating the search.
     * @return The {@code Button} for initiating the search.
     */
    public Button getSearchButton() {
        return searchButton;
    }

    /**
     * Returns the {@code ComboBox} for selecting sorting options.
     * @return The {@code ComboBox} for selecting sorting options.
     */
    public ComboBox<RowComparator> getSortingOptionDropdown() {
        return sortingOptionDropdown;
    }

}
