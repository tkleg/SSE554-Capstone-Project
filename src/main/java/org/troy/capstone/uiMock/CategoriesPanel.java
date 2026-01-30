package org.troy.capstone.uiMock;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import net.datafaker.Faker;

public class CategoriesPanel extends VBox{
    private final Set<CheckBox> categoryCheckBoxes;//Not the same as the tile pane that holds them
    //This list is meant to make it easier to retrieve which categories are selected

    public CategoriesPanel() {
        categoryCheckBoxes = new HashSet<>();
        setAlignment(Pos.CENTER_LEFT); // Align everything to the left
        
        //Add header label first (at the top)
        Label title = new Label("Categories");
        title.setStyle("-fx-font-weight: bold;");
        getChildren().add(title);
        
        int numCategories = 8;
        Faker faker = new Faker();
        for(int x = 1; x <= numCategories; x++) {
            CheckBox categoryBox = new CheckBox( faker.commerce().department() );
            getChildren().add(categoryBox);
            categoryCheckBoxes.add(categoryBox);
        }
    }

    public Set<String> getCheckedCategories() {
        return categoryCheckBoxes.stream()
            .filter(CheckBox::isSelected)
            .map(CheckBox::getText)
            .collect(Collectors.toSet());
    }
}
