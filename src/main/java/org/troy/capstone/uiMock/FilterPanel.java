package org.troy.capstone.uiMock;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FilterPanel extends VBox{
    private final Set<CheckBox> optionCheckBoxes;//Not the same as the tile pane that holds them
    //This list is meant to make it easier to retrieve which options are selected

    public FilterPanel(String title, Set<CheckBox> options) {
        optionCheckBoxes = new HashSet<>(options);
        setAlignment(Pos.CENTER_LEFT); // Align everything to the left
        
        //Add header label first (at the top) and style to bold text
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold;");
        getChildren().add(titleLabel);
        
        //Add all option CheckBoxes below the title
        getChildren().addAll(optionCheckBoxes);
    }

    public Set<String> getCheckedOptions() {
        return optionCheckBoxes.stream()
            .filter(CheckBox::isSelected)
            .map(CheckBox::getText)
            .collect(Collectors.toSet());
    }
}
