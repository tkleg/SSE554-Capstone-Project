package org.troy.capstone.ui_components.searchBar;

import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.managers.GeneralManager;
import org.troy.capstone.utils.UIUtils;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SearchBar extends HBox {
    private final TextField searchField;
    private final Button searchButton;
    
    public static SearchBar create( GeneralManager generalManager ) {
        SearchBar searchBar = new SearchBar(generalManager);
        UIUtils.setSize(searchBar, UISizeControl.SEARCH_BAR_WIDTH.getValue(), UISizeControl.SEARCH_BAR_HEIGHT.getValue());
        return searchBar;
    }

    public SearchBar( GeneralManager generalManager ) {
        searchField = new TextField();
        searchField.setPromptText("Enter Query Here");
        generalManager.addUIElement(UIElementName.SEARCH_FIELD, searchField);

        searchButton = new Button("Search");
        generalManager.setButton(searchButton);

        getChildren().addAll(searchField, searchButton);
        setSpacing(UISizeControl.WIDTH_PADDING.getValue());
    }

}
