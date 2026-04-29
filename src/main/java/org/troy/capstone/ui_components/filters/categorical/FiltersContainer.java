package org.troy.capstone.ui_components.filters.categorical;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.constants.TestFXId;
import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.entities.Item;
import org.troy.capstone.utils.UIUtils;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * The {@code FiltersContainer} class represents a UI component that contains multiple {@code FilterPanel} instances, each corresponding to a categorical attribute of the items.
 * It is the main container for all categorical filters in the UI and provides methods to generate filters based on item data and retrieve the currently selected filters.
 */
public class FiltersContainer extends ScrollPane {
    /** A map of filter types to their corresponding sets of {@code CheckBox} options in the filter panels. */
    private final Map<String, Set<CheckBox>> filterOptions;
    /** The container for all filter panels. */
    private final VBox contentContainer;
    //Define which columns are categorical for filter generation

    /**
     * Factory method to create a FiltersContainer with the appropriate size and add it to the UIElementManager.
     * 
     * @param items The items used to generate the filters.
     * @return The created {@code FiltersContainer} instance with filters generated from the item data.
     */
    public static FiltersContainer create( List<Item> items ) {
        FiltersContainer container = new FiltersContainer(items);
        UIUtils.setSize(container, UISizeControl.FILTERS_CONTAINER_WIDTH.getValue(), UISizeControl.FILTERS_CONTAINER_HEIGHT.getValue());
        return container;
    }

    /**
     * Constructor for {@code FiltersContainer}. Initializes the filter options map and content container, 
     *  then generates filters based on the provided item data.
     * 
     * @pre items should contain valid item data with categorical attributes corresponding to the expected filter types.
     * 
     * @param items The list of items used to generate the filters.
     */
    public FiltersContainer( List<Item> items ) {
        filterOptions = new HashMap<>();
        contentContainer = new VBox();
        contentContainer.setSpacing(10);
        contentContainer.setFillWidth(true);
        setContent(contentContainer);
        setFitToWidth(true);

        createFiltersFromTable(items);
    }

    /**
     * Generates filter panels based on the unique values of categorical attributes in the item data.
     * For each categorical column defined in {@code TableColumnName}, it extracts the unique values from the
     *  list of items and creates a {@code FilterPanel} with {@code CheckBox}es for each unique value. Special handling is included for the {@code TAGS} column, 
     *  which contains sets of strings.
     * 
     * @pre items should contain valid item data with categorical attributes corresponding to the expected filter types.
     * 
     * @param items The list of items used to generate the filters.
     */
    private void createFiltersFromTable(List<Item> items) {
        for (TableColumnName column : TableColumnName.getCategoricalColumns()) {
            Set<String> uniqueValues;
            //Special handling for tags since it's a Set<String> instead of a String
            if( column.getColumnName().equals(TableColumnName.TAGS.getColumnName()) ){
                uniqueValues = items.stream()
                .flatMap(item -> item.getTags().stream())
                .collect(Collectors.toSet());
            } else {
                uniqueValues = items.stream()
                    .map(item -> (String) item.getAttribute(column))
                    .collect(Collectors.toSet());
            }
            String filterTitle = column.getColumnName().substring(0, 1).toUpperCase() + column.getColumnName().substring(1).toLowerCase();
            addFilterPanel(filterTitle, uniqueValues);
        }
    }

    /**
     * Adds a filter panel to the {@code FiltersContainer} with the given title and options. Each option is represented as a {@code CheckBox}.
     * The created {@code FilterPanel} is styled with a border and added to the content container of the {@code FiltersContainer}.
     * 
     * @pre title should be a non-null string representing the filter type.
     *      options should be a non-null set of strings representing the filter options to create {@code CheckBox}es for.
     * 
     * @param title The title of the filter panel.
     * @param options The set of strings representing the filter options to create {@code CheckBox}es for.
     */
    public void addFilterPanel( String title, Set<String> options ) {
        Set<CheckBox> checkBoxes = options.stream()
            .map(CheckBox::new)
            .peek(box -> {
                String id = TestFXId.CHECKBOX_PREFIX.getId() + box.getText().toLowerCase().replaceAll("\\s+", "_").replaceAll("\\.", "");
                box.setId(id);
            })
            .collect(Collectors.toSet());
        filterOptions.put( title, checkBoxes );
        FilterPanel filterPanel = new FilterPanel(title, checkBoxes);
        UIUtils.setLineBorder(filterPanel, 2, 2);
        filterPanel.setMaxWidth(Double.MAX_VALUE); //Allow the panel to expand to fill available width
        contentContainer.getChildren().add(filterPanel);
    }

    /**
     * Retrieves the currently selected filters as a map where the keys are filter types (e.g., "Publisher", "Category")
     *  and the values are sets of selected options for each filter type respectively.
     * 
     * @pre filterOptions should be properly populated with filter types and their corresponding {@code CheckBox}es.
     *      The {@code CheckBox}es should reflect the current user selections.
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
