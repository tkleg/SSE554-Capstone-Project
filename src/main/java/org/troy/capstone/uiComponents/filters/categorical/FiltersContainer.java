package org.troy.capstone.uiComponents.filters.categorical;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.constants.uiElementName;
import org.troy.capstone.constants.uiSizeControls;
import org.troy.capstone.data_structures.ItemTable.ItemHashMap;
import org.troy.capstone.managers.GeneralManager;
import org.troy.capstone.utils.UIUtils;

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
    //Define which columns are categorical for filter generation

    public static FiltersContainer create( GeneralManager generalManager, ItemHashMap itemHashMap ) {
        FiltersContainer container = new FiltersContainer(generalManager, itemHashMap);
        UIUtils.setSize(container, uiSizeControls.FILTERS_CONTAINER_WIDTH, uiSizeControls.FILTERS_CONTAINER_HEIGHT);
        generalManager.addUIElement(uiElementName.FILTERS_CONTAINER, container);

        return container;
    }
    public FiltersContainer( GeneralManager generalManager, ItemHashMap itemHashMap ) {
        filterOptions = new HashMap<>();
        contentContainer = new VBox();
        contentContainer.setSpacing(10);
        contentContainer.setFillWidth(true);
        setContent(contentContainer);
        setFitToWidth(true);

        createFiltersFromTable(itemHashMap);
    }

    private void createFiltersFromTable(ItemHashMap itemHashMap) {
        for (tableColumns column : tableColumns.getCategoricalColumns()) {
            Set<String> uniqueValues;
            if( column.getColumnName().equals(tableColumns.TAGS.getColumnName()) ){// Special handling for tags since it's a set of strings
                uniqueValues = itemHashMap.values().stream()
                .flatMap(item -> item.getTags().stream())
                .collect(Collectors.toSet());
            } else {
                uniqueValues = itemHashMap.values().stream()
                    .map(item -> (String) item.getAttribute(column))
                    .collect(Collectors.toSet());
            }
            String filterTitle = column.getColumnName().substring(0, 1).toUpperCase() + column.getColumnName().substring(1).toLowerCase();
            addFilterPanel(filterTitle, uniqueValues);
        }
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
