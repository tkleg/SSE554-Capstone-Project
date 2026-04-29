package org.troy.capstone.ui_components.filters.categorical;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.troy.capstone.constants.TestFXId;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 * The {@code FilterPanel} class represents a UI component that displays a set of categorical filter options as checkboxes within a titled pane.
 */
public class FilterPanel extends TitledPane{
    /** A set of {@code CheckBox} objects representing the filter options in this panel. */
    private final Set<CheckBox> optionCheckBoxes;//Not the same as the tile pane that holds them
    //This list is meant to make it easier to retrieve which options are selected

    /**
     * Creates a filter panel with the given title and options.
     * The options are expected to be {@code CheckBox} objects that are already configured with the appropriate text and event handlers.
     * 
     * @param title The title of the filter panel.
     * @param options The set of {@code CheckBox} objects representing the filter options.
     */
    public FilterPanel(String title, Set<CheckBox> options) {
        optionCheckBoxes = new HashSet<>(options);
        VBox contentBox = new VBox(5);
        super(title, contentBox);

        setExpanded(false);

        setAlignment(Pos.CENTER_LEFT);

        //Style only the title text, not the content
        setStyle("-fx-font-weight: bold");
        
        //Ensure the content (checkboxes) use normal font weight
        contentBox.setStyle("-fx-font-weight: normal;");

        contentBox.getChildren().addAll(optionCheckBoxes);
        
        setId(TestFXId.FILTER_PANEL_PREFIX.getId() + title.toLowerCase().replaceAll("\\s+", "_"));

    }

    /**
     * Gets the currently checked options in the filter panel.
     * @return The set of option texts that are currently selected (checked).
     */
    public Set<String> getCheckedOptions() {
        return optionCheckBoxes.stream()
            .filter(CheckBox::isSelected)
            .map(CheckBox::getText)
            .collect(Collectors.toSet());
    }

}
