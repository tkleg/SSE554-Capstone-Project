package org.troy.capstone.ui_components.filters.categorical;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.CheckBox;

public class FilterPanelTest {

    @BeforeAll
    public static void setup() {
        new JFXPanel();
    }

    @Test
    @DisplayName("Test constructor with filled options")
    public void testConstructorWithFilledOptions() {
        Set<String> options = Set.of("Option 1", "Option 2", "Option 3");
        Set<CheckBox> checkBoxes = options.stream()
            .map(CheckBox::new)
            .peek(CheckBox::fire)//Sets checkboxes to selected
            .collect(Collectors.toSet());
        FilterPanel filterPanel = new FilterPanel("Test Filter", checkBoxes);
        assert filterPanel.getCheckedOptions().size() == 3 : "Expected 3 options in the filter panel, but got: " + filterPanel.getCheckedOptions().size();
        assert filterPanel.getCheckedOptions().containsAll(options) : "Expected options to be in the filter panel, but got: " + filterPanel.getCheckedOptions();
    }

    @Test
    @DisplayName("Test getCheckedOptions with no options selected")
    public void testGetCheckedOptionsWithNoOptionsSelected() {
        Set<String> options = Set.of("Option 1", "Option 2", "Option 3");
        Set<CheckBox> checkBoxes = options.stream()
            .map(CheckBox::new)
            .collect(Collectors.toSet());
        FilterPanel filterPanel = new FilterPanel("Test Filter", checkBoxes);
        assert filterPanel.getCheckedOptions().isEmpty() : "Expected no checked options, but got: " + filterPanel.getCheckedOptions();
    }

    @Test
    @DisplayName("Test getCheckedOptions with some options selected")
    public void testGetCheckedOptionsWithSomeOptionsSelected() {
        List<CheckBox> checkBoxes = List.of(
            new CheckBox("Option 1"),
            new CheckBox("Option 2"),
            new CheckBox("Option 3")
        );
        checkBoxes.get(0).fire();
        Set<CheckBox> checkBoxSet = Set.copyOf(checkBoxes);
        
        FilterPanel filterPanel = new FilterPanel("Test Filter", checkBoxSet);
        Set<String> checkedOptions = filterPanel.getCheckedOptions();
        assert checkedOptions.size() == 1 : "Expected 1 checked option, but got: " + checkedOptions.size();
        assert checkedOptions.contains("Option 1") : "Expected 'Option 1' to be checked, but it was not.";
    }

}
