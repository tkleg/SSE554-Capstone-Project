package org.troy.capstone.ui_components.search_bar;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.managers.GeneralManager;
import org.troy.capstone.utils.TableUtils;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import tech.tablesaw.api.Table;

public class SearchBarTest {

    private static GeneralManager generalManager;
    private static Table table;
    private SearchBar searchBar;

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() {
        new JFXPanel();
        table = TableUtils.readCleanedAttributedData();
        generalManager = new GeneralManager(table);
    }

    @BeforeEach
    public void setUp() {
        searchBar = SearchBar.create(generalManager);
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

}
