package org.troy.capstone.uiMock;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SearchBar extends HBox {
    private TextField searchField;
    private final Button searchButton;
    private List<EventHandler<ActionEvent>> additionalActions;
    
    public SearchBar() {
        additionalActions = new ArrayList<>();
        searchField = new TextField();
        searchField.setPromptText("Enter Query Here");

        searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            System.out.println( "Search Query: " + searchField.getText() );

            for (EventHandler<ActionEvent> action : additionalActions)
                action.handle(e);
        });

        getChildren().addAll(searchField, searchButton);
        setSpacing(5); // 5px spacing between elements
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
