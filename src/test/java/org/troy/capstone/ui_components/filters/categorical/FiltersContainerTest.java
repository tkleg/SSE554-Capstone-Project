package org.troy.capstone.ui_components.filters.categorical;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.managers.GeneralManager;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import tech.tablesaw.api.Table;

public class FiltersContainerTest {
    private final Table table = TestDataHolder.getTableCopy();
    private final ItemHashMap itemHashMap = TestDataHolder.getItemHashMapCopy();
    private GeneralManager generalManager;
    private FiltersContainer filtersContainer;

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() {
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() {
        generalManager = new GeneralManager(table);
        filtersContainer = FiltersContainer.create(itemHashMap);
        generalManager.addUIElement(UIElementName.FILTERS_CONTAINER, filtersContainer);
    }

    @Test
    @DisplayName("Test FiltersContainer creation and filter generation with valid data")
    public void testFiltersContainerCreation() {        
        assert filtersContainer.getSelectedFilters().size() == 3 : "Expected 3 selected filters, but got: " + filtersContainer.getSelectedFilters();
        assert filtersContainer.getSelectedFilters().keySet().containsAll(Set.of("Publisher", "Category", "Tags")) : "Expected filter keys to contain 'Publisher', 'Category', and 'Tags', but got: " + filtersContainer.getSelectedFilters().keySet();
    }

    @Test
    @DisplayName("Test getSelectedFilters with a selected filter")
    @SuppressWarnings("unchecked")
    public void testGetSelectedFiltersWithSelectedFilter() {
        //Access the checkbox directly from the filtersContainer's filterOptions
        try {
            Field filterOptionsField = FiltersContainer.class.getDeclaredField("filterOptions");
            filterOptionsField.setAccessible(true);
            Map<String, Set<CheckBox>> filterOptions = (Map<String, Set<CheckBox>>) filterOptionsField.get(filtersContainer);

            filterOptions.get("Tags").stream()
                .findFirst()
                .ifPresent(checkbox -> checkbox.setSelected(true));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Failed to access filterOptions via reflection: " + e.getMessage());
            throw new RuntimeException("Failed to access filterOptions via reflection", e);
        }

        System.out.println("Selected filters after selecting a tag: " + filtersContainer.getSelectedFilters());
        assert filtersContainer.getSelectedFilters().get("Tags").size() == 1 : "Expected 1 selected tag filter, but got: " + filtersContainer.getSelectedFilters().get("Tags").size();
        assert filtersContainer.getSelectedFilters().get("Category").isEmpty() : "Expected no selected category filters, but got: " + filtersContainer.getSelectedFilters().get("Category");
        assert filtersContainer.getSelectedFilters().get("Publisher").isEmpty() : "Expected no selected publisher filters, but got: " + filtersContainer.getSelectedFilters().get("Publisher");
    }

    @Test
    @DisplayName("Test that the generalManager has the filtersContainer and it is cast properly")
    public void testFilterContainerInGeneralManager(){
        Optional<Node> filterContainerNode = generalManager.getUIElement(UIElementName.FILTERS_CONTAINER);
        assert filterContainerNode.isPresent() : "Expected generalManager to have a filter container node present, but it was not found.";
        assert filterContainerNode.get() instanceof FiltersContainer : "Expected filter container node to be an instance of FiltersContainer, but got: " + filterContainerNode.get().getClass();
    }
}