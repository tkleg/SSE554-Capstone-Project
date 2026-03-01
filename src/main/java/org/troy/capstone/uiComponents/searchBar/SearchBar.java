package org.troy.capstone.uiComponents.searchBar;

import java.util.ArrayList;
import java.util.List;

import org.troy.capstone.constants.uiElementName;
import org.troy.capstone.constants.uiSizeControls;
import org.troy.capstone.managers.GeneralManager;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SearchBar extends HBox {
    private final TextField searchField;
    private final Button searchButton;
    private final List<EventHandler<ActionEvent>> additionalActions;
    
    public SearchBar( GeneralManager generalManager ) {
        additionalActions = new ArrayList<>();
        searchField = new TextField();
        searchField.setPromptText("Enter Query Here");
        generalManager.addUIElement(uiElementName.SEARCH_FIELD, searchField);

        searchButton = new Button("Search");
        generalManager.addUIElement(uiElementName.SEARCH_BUTTON, searchButton);

        /*searchButton.setOnAction(e -> {
            System.out.println( "Search Query: " + searchField.getText() );

            for (EventHandler<ActionEvent> action : additionalActions)
                action.handle(e);
        });*/
        searchButton.setOnAction( e ->{
            System.out.println( generalManager.getSearchData() );
        });

        getChildren().addAll(searchField, searchButton);
        setSpacing(uiSizeControls.WIDTH_PADDING);

        setPrefWidth(uiSizeControls.SEARCH_BAR_WIDTH);
        setMaxWidth(uiSizeControls.SEARCH_BAR_WIDTH);
        setMinWidth(uiSizeControls.SEARCH_BAR_WIDTH);
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
