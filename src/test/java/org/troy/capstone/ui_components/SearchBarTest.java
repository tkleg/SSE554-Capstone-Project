package org.troy.capstone.ui_components;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.TestUtils;
import org.troy.capstone.constants.TestFXId;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.managers.general.GeneralManager;
import org.troy.capstone.search_engine.sorting.RowComparator;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SearchBarTest extends ApplicationTest {

    private static GeneralManager generalManager;
    private SearchBar searchBar;

    @Override
    @Start
    public void start(Stage stage) {
        searchBar = new SearchBar();
        generalManager = new GeneralManager(TestDataHolder.getTableCopy());
        generalManager.addUIElement(UIElementName.SEARCH_FIELD, searchBar.getSearchField());
        generalManager.addUIElement(UIElementName.SORTING_OPTION_DROPDOWN, searchBar.getSortingOptionDropdown());
        generalManager.setButton(searchBar.getSearchButton());
        Scene scene = new Scene(searchBar, 800, 100);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() {
        new JFXPanel();
        generalManager = new GeneralManager(TestDataHolder.getTableCopy());
    }

    @BeforeEach
    public void setUp() {
        searchBar = new SearchBar();
        generalManager.addUIElement(UIElementName.SEARCH_FIELD, searchBar.getSearchField());
        generalManager.addUIElement(UIElementName.SORTING_OPTION_DROPDOWN, searchBar.getSortingOptionDropdown());
        generalManager.setButton(searchBar.getSearchButton());
    }
    
    @Test
    @DisplayName("Test SearchBar creation and initial state")
    public void testSearchBarCreation() {
        assertNotNull(searchBar, "SearchBar should be created successfully");
        assertEquals("Enter Query Here", searchBar.getSearchField().getPromptText(), "Search field should have correct prompt text");
        assertEquals("Search", searchBar.getSearchButton().getText(), "Search button should have correct text");
    }

    @Test
    @DisplayName("Test SearchBar UI elements are registered in GeneralManager")
    public void testSearchBarUIElementsRegisteredInGeneralManager() {
        assertNotNull(generalManager.getUIElement(UIElementName.SEARCH_FIELD), "Search field should be registered in GeneralManager");
        assertNotNull(generalManager.getButton(), "Search button should be registered in UIElementManager through GeneralManager");
    }

    @Test
    @DisplayName("Test query after setting search field text and clicking search button")
    public void testSearchBarQueryAfterSettingTextAndClickingButton() {
        String query = "test query";
        searchBar.getSearchField().setText(query);
        searchBar.getSearchButton().fire();

        assertEquals(query, searchBar.getSearchField().getText(), "Search field should contain the correct text after clicking search button");
        
        Optional<Node> searchField = generalManager.getUIElement(UIElementName.SEARCH_FIELD);
        assert searchField.isPresent() : "Search data should contain the search field value after clicking search button";
        Node node = searchField.get();
        assert node instanceof TextField : "Node retrieved is a textfield for the searchField";
        TextField textField = (TextField) node;
        assertEquals(query, textField.getText(), "Search field should contain the correct text after clicking search button");
    }

    @Test
    @DisplayName("Test that generalManager has the search button and it is cast properly")
    public void testSearchButtonInGeneralManager() {
        Button searchButton = generalManager.getButton();
        assertNotNull(searchButton, "Expected generalManager to have a search button, but it was not found.");
        assert searchButton instanceof Button : "Expected search button to be an instance of Button, but got: " + searchButton.getClass();
    }

    @Test
    @DisplayName("Test the text shown when clicking the sortBy dropdown")
    public void testSortByDropdownText() throws NoSuchFieldException, IllegalAccessException {
        
        ComboBox<RowComparator> dropdown = TestUtils.lookupByTestFXId(TestFXId.SORT_OPTION_DROPDOWN);

        assertNotNull(dropdown, "Sorting option dropdown should not be null");

        //Open the dropdown and check the text in the cell
        interact(dropdown::show);
        RowComparator thirdItem = dropdown.getItems().get(2);
        RowComparator expectedComparator = new RowComparator(RowComparator.SortType.RELEVANCE_ASCENDING);
        assertEquals(expectedComparator, thirdItem, "3rd item should be /\"" + expectedComparator.toString() + "/\" but got: " + thirdItem.toString());
        String thirdItemText = thirdItem.toString();
        String expectedText = "Relevance Ascending";
        assertEquals(expectedText, thirdItemText, "3rd item text should by /\"" + expectedText + "/\" but got: " + thirdItemText);

        //Click the 3rd item and check that the dropdown value is updated
        interact(() -> dropdown.getSelectionModel().select(2));
        RowComparator selectedComparator = dropdown.getValue();
        assertNotNull(selectedComparator, "Selected RowComparator should not be null after selecting an item from the dropdown");
        assertEquals(expectedComparator, selectedComparator, "Selected RowComparator should be equal to the expected comparator after selecting an item from the dropdown, but got: " + selectedComparator);
        assertEquals(expectedText, selectedComparator.toString(), "Selected RowComparator should have the expected text representation after selecting an item from the dropdown, but got: " + selectedComparator.toString());
    }

    @Test
    @DisplayName("Test null options in the sortBy dropdown")
    public void testNullOptionsInSortByDropdown() throws NoSuchFieldException, IllegalAccessException {
        ComboBox<RowComparator> dropdown = TestUtils.lookupByTestFXId(TestFXId.SORT_OPTION_DROPDOWN);

        assertNotNull(dropdown, "Sorting option dropdown should not be null");

        //Add a null item to the dropdown and check that it is displayed as "None"
        interact(() -> dropdown.getSelectionModel().clearSelection());
        interact(() -> dropdown.setValue(null));

        //No assertions needed, just checking that no exceptions are thrown
    }
}
