package org.troy.capstone.ui_components.filters.categorical;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.data_structures.ItemTable.ItemHashMap;
import org.troy.capstone.managers.GeneralManager;
import org.troy.capstone.utils.TableUtils;

import javafx.embed.swing.JFXPanel;
import tech.tablesaw.api.Table;

public class FiltersContainerTest {
    private Table table;
    private ItemHashMap itemHashMap;
    private GeneralManager generalManager;
    private FiltersContainer filtersContainer;

    @BeforeAll
    public static void setup() {
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() {
        table = TableUtils.readCleanedAttributedData();
        itemHashMap = ItemHashMap.fromTable(table);
        generalManager = new GeneralManager(table);
        filtersContainer = FiltersContainer.create(generalManager, itemHashMap);
    }

    @Test
    @DisplayName("Test FiltersContainer creation and filter generation with valid data")
    public void testFiltersContainerCreation() {        
        assert filtersContainer.getSelectedFilters().size() == 3 : "Expected 3 selected filters, but got: " + filtersContainer.getSelectedFilters();
        assert filtersContainer.getSelectedFilters().keySet().containsAll(Set.of("Publisher", "Category", "Tags")) : "Expected filter keys to contain 'Publisher', 'Category', and 'Tags', but got: " + filtersContainer.getSelectedFilters().keySet();
    }

    @Test
    @DisplayName("Test getSelectedFilters with a selected filter")
    public void testGetSelectedFiltersWithSelectedFilter() {
        //Access the checkbox directly from the filtersContainer's filterOptions
        filtersContainer.getFilterOptions().get("Tags").stream()
            .findFirst()
            .ifPresent(checkbox -> checkbox.setSelected(true));
        
        System.out.println("Selected filters after selecting a tag: " + filtersContainer.getSelectedFilters());
        assert filtersContainer.getSelectedFilters().get("Tags").size() == 1 : "Expected 1 selected tag filter, but got: " + filtersContainer.getSelectedFilters().get("Tags").size();
        assert filtersContainer.getSelectedFilters().get("Category").isEmpty() : "Expected no selected category filters, but got: " + filtersContainer.getSelectedFilters().get("Category");
        assert filtersContainer.getSelectedFilters().get("Publisher").isEmpty() : "Expected no selected publisher filters, but got: " + filtersContainer.getSelectedFilters().get("Publisher");
    }
}