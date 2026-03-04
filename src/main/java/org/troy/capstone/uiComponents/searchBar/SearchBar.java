package org.troy.capstone.uiComponents.searchBar;

import java.util.ArrayList;
import java.util.List;

import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.managers.GeneralManager;
import org.troy.capstone.utils.UIUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SearchBar extends HBox {
    private final TextField searchField;
    private final Button searchButton;
    private final List<EventHandler<ActionEvent>> additionalActions;
    
    public static SearchBar create( GeneralManager generalManager ) {
        SearchBar searchBar = new SearchBar(generalManager);
        UIUtils.setSize(searchBar, UISizeControl.SEARCH_BAR_WIDTH.getValue(), UISizeControl.SEARCH_BAR_HEIGHT.getValue());
        return searchBar;
    }

    public SearchBar( GeneralManager generalManager ) {
        additionalActions = new ArrayList<>();
        searchField = new TextField();
        searchField.setPromptText("Enter Query Here");
        generalManager.addUIElement(UIElementName.SEARCH_FIELD, searchField);

        searchButton = new Button("Search");
        generalManager.setButton(searchButton);

        getChildren().addAll(searchField, searchButton);
        setSpacing(UISizeControl.WIDTH_PADDING.getValue());
    }
    
    public void addAdditionalAction(EventHandler<ActionEvent> action) {
        additionalActions.add(action);
    }
    
    public String getSearchText() {
        return searchField.getText();
    }
    
    public TextField getSearchField() {
        return searchField;
    }
}
