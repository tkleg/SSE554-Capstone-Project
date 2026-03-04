package org.troy.capstone.managers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.data_structures.ItemTable.ItemHashMap;
import org.troy.capstone.uiComponents.filters.categorical.FiltersContainer;
import org.troy.capstone.uiComponents.filters.stars.StarRatingFilter;
import org.troy.capstone.utils.TableUtils;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import tech.tablesaw.api.Table;

public class GeneralManagerTest {

    private static final Table table = TableUtils.readCleanedAttributedData();
    private static final GeneralManager GM = new GeneralManager(table);
    private static final GeneralManager fullGM = new GeneralManager(table);
    private static Button fullGMButton;

    @BeforeAll
    public static void setup() {
        // Initialize JavaFX environment
        new JFXPanel(); // This will initialize the JavaFX toolkit

        fullGMButton = new Button("Search");
        fullGM.addUIElement(UIElementName.MIN_PRICE_SLIDER, new Slider(0, 100, 25));
        fullGM.addUIElement(UIElementName.MAX_PRICE_SLIDER, new Slider(0, 100, 75));
        fullGM.addUIElement(UIElementName.SEARCH_FIELD, new TextField("Test Query"));

        FiltersContainer filtersContainer = FiltersContainer.create(fullGM, ItemHashMap.fromTable(TableUtils.readCleanedAttributedData()));
        fullGM.addUIElement(UIElementName.FILTERS_CONTAINER, filtersContainer);
        fullGM.addUIElement(UIElementName.STAR_RATING_FILTER, StarRatingFilter.create(fullGM));
        fullGM.setButton(fullGMButton);
        fullGMButton.setId("fullGMButton");
    }

    @BeforeEach
    public void clearGM(){
        GM.clearUIElements();
    }

    @Test
    @DisplayName("Test addUIElement with only invalid Button UI element added")
    public void testAddUIElementWithOnlyInvalidButtonUIElement() {
        GM.addUIElement(UIElementName.SEARCH_BUTTON, new HBox());
        assert GM.getSearchData().isEmpty() : "Expected empty search data when only invalid UI elements are added, but got: " + GM.getSearchData();
    }

    @Test
    @DisplayName("Test addUIElement with valid and invalid UI elements added")
    public void testAddUIElementWithValidAndInvalidUIElements() {
        GM.addUIElement(UIElementName.MIN_PRICE_SLIDER, new HBox());
        GM.addUIElement(UIElementName.SEARCH_BUTTON, new Button("Search"));
        assert GM.getSearchData().isEmpty() : "Expected empty search data when only invalid UI elements are added, but got: " + GM.getSearchData();
    }

    @Test
    @DisplayName("Test addUIElement with a UI element and a Button")
    public void testAddUIElementWithSomeValidUIElements() {
        GM.addUIElement(UIElementName.MIN_PRICE_SLIDER, new Slider());
        GM.setButton(new Button());
        assert GM.getSearchData().size() == 1 : "Expected search data to contain 1 entry when valid UI elements are added, but got: " + GM.getSearchData();
    }

    @Test
    @DisplayName("Test addUIElement with all valid UI elements")
    public void testaddUIElementWithAllElements() {
        assert fullGM.getSearchData().size() == 5 : "Expected search data to contain 5 entries when multiple valid UI elements are added, but got: " + fullGM.getSearchData();
    }

    @Test
    @DisplayName("Test printed results with full GM setup")
    public void testPrintedResultsWithFullGMSetup() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        fullGMButton.fire();
        
        String output = outContent.toString();
        System.out.println("Captured Output: " + output); // Print the captured output for debugging
        System.setOut(originalOut);

        assert output.contains("Search Data") : "Expected output to contain 'Search Data', but got: " + output;
        assert output.contains("SEARCH_QUERY=Test Query") : "Expected output to contain 'SEARCH_QUERY=Test Query', but got: " + output;
        assert output.contains("MIN_PRICE=25.0") : "Expected output to contain 'MIN_PRICE=25.0', but got: " + output;
        assert output.contains("MAX_PRICE=75.0") : "Expected output to contain 'MAX_PRICE=75.0', but got: " + output;
        assert output.contains("Number of results: 64") : "Expected output to contain 'Number of results: 64', but got: " + output;
        assert output.contains("FILTERS_CONTAINER") : "Expected output to contain 'FILTERS_CONTAINER', but got: " + output;
        assert output.contains("MIN_STAR_RATING=0") : "Expected output to contain 'MIN_STAR_RATING=0', but got: " + output;
        assert output.contains("Category=[]") : "Expected output to contain 'Category=[]', but got: " + output;
        assert output.contains("Publisher=[]") : "Expected output to contain 'Publisher=[]', but got: " + output;
        assert output.contains("Tags=[]") : "Expected output to contain 'Tags=[]', but got: " + output;
    }

    @Test
    @DisplayName("Test printed results for empty GM")
    public void testPrintedResultsWithEmptyGM() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Button button = new Button("Search");
        GM.setButton(button);

        button.fire();

        String output = outContent.toString();
        System.out.println("Captured Output: " + output); // Print the captured output for debugging
        System.setOut(originalOut);

        assert output.contains("Search Data: {}") : "Expected output to contain 'Search Data: {}', but got: " + output;
        assert output.contains( "Number of results: 961" ) : "Expected output to contain 'Number of results: 961', but got: " + output;
    }
    
}
