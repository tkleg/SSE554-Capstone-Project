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
        generalManager = new GeneralManager(table, itemHashMap);
        filtersContainer = FiltersContainer.create(itemHashMap.getItemsAsList());
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
        Map<String, FilterPanel> filterOptions;
        try{
            Field filterOptionsField = FiltersContainer.class.getDeclaredField("filterOptions");
            filterOptionsField.setAccessible(true);
            filterOptions = (Map<String, FilterPanel>) filterOptionsField.get(filtersContainer);
        }catch(ReflectiveOperationException e){
            assert false : "Reflection failed to access filterOptions field: " + e.getMessage();
            return;
        }

        try{
            Field optionCheckboxesField = FilterPanel.class.getDeclaredField("optionCheckBoxes");
            optionCheckboxesField.setAccessible(true);
            Set<CheckBox> optionCheckboxes = (Set<CheckBox>) optionCheckboxesField.get(filterOptions.get("Tags"));
            optionCheckboxes.stream()
                .filter(cb -> cb.getText().equals("Bestseller"))
                .forEach(cb -> cb.setSelected(true));
        }catch(ReflectiveOperationException e){
            assert false : "Reflection failed to access optionCheckBoxes field: " + e.getMessage();
            return;
        }
        
        Map<String, Set<String>> selectedFilters = filtersContainer.getSelectedFilters();
        System.out.println("Selected filters after selecting a tag: " + selectedFilters);
        assert selectedFilters.get("Tags").size() == 1 : "Expected 1 selected tag filter, but got: " + selectedFilters.get("Tags").size();
        assert selectedFilters.get("Category").isEmpty() : "Expected no selected category filters, but got: " + selectedFilters.get("Category");
        assert selectedFilters.get("Publisher").isEmpty() : "Expected no selected publisher filters, but got: " + selectedFilters.get("Publisher");
    }

    @Test
    @DisplayName("Test that the generalManager has the filtersContainer and it is cast properly")
    public void testFilterContainerInGeneralManager(){
        Optional<Node> filterContainerNode = generalManager.getUIElement(UIElementName.FILTERS_CONTAINER);
        assert filterContainerNode.isPresent() : "Expected generalManager to have a filter container node present, but it was not found.";
        assert filterContainerNode.get() instanceof FiltersContainer : "Expected filter container node to be an instance of FiltersContainer, but got: " + filterContainerNode.get().getClass();
    }
}