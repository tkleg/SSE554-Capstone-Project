package org.troy.capstone.uiMock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class FiltersContainer extends ScrollPane {
    private final Map<String, Set<CheckBox>> filterOptions;
    private final VBox contentContainer;

    public FiltersContainer() {
        filterOptions = new HashMap<>();
        contentContainer = new VBox();
        setContent(contentContainer);
    }

    public void addFilterPanel( String title, Set<String> options ) {
        Set<CheckBox> checkBoxes = new HashSet<>();
        for (String option : options)
            checkBoxes.add(new CheckBox(option));
        filterOptions.put( title, checkBoxes );
        contentContainer.getChildren().add( new FilterPanel(title, options) );
    }

    public Map<String, Set<String>> getSelectedFilters() {
        Map<String, Set<String>> selectedFilters = new HashMap<>();
        for (String filterType : filterOptions.keySet()) {
            Set<String> selectedOptions = filterOptions.get(filterType)
                    .stream()
                    .filter(CheckBox::isSelected)
                    .map(CheckBox::getText)
                    .collect(Collectors.toSet());
            selectedFilters.put(filterType, selectedOptions);
        }
        return selectedFilters;
    }
}
