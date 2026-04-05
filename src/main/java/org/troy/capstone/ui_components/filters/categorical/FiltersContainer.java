package org.troy.capstone.ui_components.filters.categorical;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.utils.UIUtils;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * The FiltersContainer class represents a UI component that contains multiple FilterPanel instances, each corresponding to a categorical attribute of the items.
 * It is the main container for all categorical filters in the UI and provides methods to generate filters based on item data and retrieve the currently selected filters.
 */
public class FiltersContainer extends ScrollPane {
    /** A map of filter types to their corresponding sets of CheckBox options in the filter panels. */
    private final Map<String, Set<CheckBox>> filterOptions;
    /** The container for all filter panels. */
    private final VBox contentContainer;
    //Define which columns are categorical for filter generation

    /**
     * Factory method to create a FiltersContainer with the appropriate size and add it to the UIElementManager.
     * 
     * @param itemHashMap The item hash map containing all items, used to extract unique values for filter generation.
     * @return The created FiltersContainer instance with filters generated from the item data.
     */
    public static FiltersContainer create( ItemHashMap itemHashMap ) {
        FiltersContainer container = new FiltersContainer(itemHashMap);
        UIUtils.setSize(container, UISizeControl.FILTERS_CONTAINER_WIDTH.getValue(), UISizeControl.FILTERS_CONTAINER_HEIGHT.getValue());
        return container;
    }

    /**
     * Constructor for FiltersContainer. Initializes the filter options map and content container, 
     *  then generates filters based on the provided item data.
     * 
     * @pre itemHashMap should contain valid item data with categorical attributes corresponding to the expected filter types.
     * 
     * @param itemHashMap The item hash map containing all items, used to extract unique values for filter generation.
     */
    public FiltersContainer( ItemHashMap itemHashMap ) {
        filterOptions = new HashMap<>();
        contentContainer = new VBox();
        contentContainer.setSpacing(10);
        contentContainer.setFillWidth(true);
        setContent(contentContainer);
        setFitToWidth(true);

        createFiltersFromTable(itemHashMap);
    }

    /**
     * Generates filter panels based on the unique values of categorical attributes in the item data.
     * For each categorical column defined in TableColumnName, it extracts the unique values from the
     *  itemHashMap and creates a FilterPanel with CheckBoxes for each unique value. Special handling is included for the TAGS column, 
     *  which contains sets of strings.
     * 
     * @pre itemHashMap should contain valid item data with categorical attributes corresponding to the expected filter types.
     * 
     * @param itemHashMap The item hash map containing all items, used to extract unique values for filter generation.
     */
    private void createFiltersFromTable(ItemHashMap itemHashMap) {
        for (TableColumnName column : TableColumnName.getCategoricalColumns()) {
            Set<String> uniqueValues;
            if( column.getColumnName().equals(TableColumnName.TAGS.getColumnName()) ){// Special handling for tags since it's a set of strings
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

    /**
     * Adds a filter panel to the FiltersContainer with the given title and options. Each option is represented as a CheckBox.
     * The created FilterPanel is styled with a border and added to the content container of the FiltersContainer.
     * 
     * @pre title should be a non-null string representing the filter type.
     *      options should be a non-null set of strings representing the filter options to create CheckBoxes for.
     * 
     * @param title The title of the filter panel.
     * @param options The set of strings representing the filter options to create CheckBoxes for.
     */
    public void addFilterPanel( String title, Set<String> options ) {
        Set<CheckBox> checkBoxes = new HashSet<>();
        for (String option : options)
            checkBoxes.add(new CheckBox(option));
        filterOptions.put( title, checkBoxes );
        FilterPanel filterPanel = new FilterPanel(title, checkBoxes);
        UIUtils.setLineBorder(filterPanel, 2, 2);
        filterPanel.setMaxWidth(Double.MAX_VALUE); // Allow the panel to expand to fill available width
        contentContainer.getChildren().add(filterPanel);
    }

    /**
     * Retrieves the currently selected filters as a map where the keys are filter types (e.g., "Publisher", "Category")
     *  and the values are sets of selected options for each filter type respectively.
     * 
     * @pre filterOptions should be properly populated with filter types and their corresponding CheckBoxes.
     *      The CheckBoxes should reflect the current user selections.
     * 
     * @return selectedFilters A map containing the currently selected filters.
     */
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
