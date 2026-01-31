package org.troy.capstone.uiMock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class FiltersContainer extends ScrollPane {
    private final Map<String, Set<CheckBox>> filterOptions;
    private final VBox contentContainer;

    public FiltersContainer() {
        filterOptions = new HashMap<>();
        contentContainer = new VBox();
        contentContainer.setSpacing(10); // Add spacing between filter panels
        contentContainer.setFillWidth(true); // Make children fill the available width
        setContent(contentContainer);
        setFitToWidth(true); // Make the ScrollPane's content fit to the width
        setPrefSize(400, 250);
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
    }

    public void addFilterPanel( String title, Set<String> options ) {
        Set<CheckBox> checkBoxes = new HashSet<>();
        for (String option : options)
            checkBoxes.add(new CheckBox(option));
        filterOptions.put( title, checkBoxes );
        FilterPanel filterPanel = new FilterPanel(title, checkBoxes);
        filterPanel.setBorder(new Border(new BorderStroke(
            Color.BLACK, 
            BorderStrokeStyle.SOLID, 
            new CornerRadii(2), 
            new BorderWidths(2)
        )));
        filterPanel.setMaxWidth(Double.MAX_VALUE); // Allow the panel to expand to fill available width
        contentContainer.getChildren().add(filterPanel);
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
