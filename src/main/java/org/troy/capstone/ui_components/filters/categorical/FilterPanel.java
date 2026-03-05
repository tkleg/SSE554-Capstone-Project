package org.troy.capstone.ui_components.filters.categorical;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class FilterPanel extends TitledPane{
    private final Set<CheckBox> optionCheckBoxes;//Not the same as the tile pane that holds them
    //This list is meant to make it easier to retrieve which options are selected

    /**
     * Creates a filter panel with the given title and options.
     * The options are expected to be CheckBoxes that are already configured with the appropriate text and event handlers.
     * @param title (String) : The title of the filter panel
     * @param options (Set<CheckBox>) : The set of CheckBoxes representing the filter options.
     */
    public FilterPanel(String title, Set<CheckBox> options) {
        optionCheckBoxes = new HashSet<>(options);
        VBox contentBox = new VBox(5); // 5px spacing between checkboxes
        super(title, contentBox);

        setExpanded(false);

        setAlignment(Pos.CENTER_LEFT);

        //Style only the title text, not the content
        setStyle("-fx-font-weight: bold");
        
        //Ensure the content (checkboxes) use normal font weight
        contentBox.setStyle("-fx-font-weight: normal;");

        contentBox.getChildren().addAll(optionCheckBoxes);

    }

    /**
     * Gets the currently checked options in the filter panel.
     * @return Set<String> : The set of option texts that are currently selected (checked).
     */
    public Set<String> getCheckedOptions() {
        return optionCheckBoxes.stream()
            .filter(CheckBox::isSelected)
            .map(CheckBox::getText)
            .collect(Collectors.toSet());
    }

    /**
     * Gets the CheckBox objects representing the filter options.
     * @return Set<CheckBox> : The set of CheckBox objects representing the filter options.
     */
    public Set<CheckBox> getOptionCheckBoxes() {
        return optionCheckBoxes;
    }

}
