package org.troy.capstone.uiMock;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SearchBar extends HBox {
    private TextField searchField;
    private final Button searchButton;
    
    public SearchBar() {
        searchField = new TextField();
        searchField.setPromptText("Enter Query Here");

        searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            System.out.println( searchField.getText() );
        });

        getChildren().addAll(searchField, searchButton);
        setSpacing(5); // 5px spacing between elements
    }
}
