package org.troy.capstone.managers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.ui_components.filters.StarRatingFilter;
import org.troy.capstone.ui_components.filters.categorical.FiltersContainer;
import org.troy.capstone.utils.TableUtils;

import javafx.scene.Node;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import tech.tablesaw.api.Table;

import java.lang.reflect.Field;
import java.util.Map;

public class GeneralManagerTest {

    private static final Table table = TestDataHolder.getTableCopy();
    private static final GeneralManager GM = new GeneralManager(table);
    private static final GeneralManager fullGM = new GeneralManager(table);
    private static Button fullGMButton;

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
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
    @SuppressWarnings("unchecked")
    public void clearGM() throws NoSuchFieldException, IllegalAccessException {
        Field uiManagerField = GeneralManager.class.getDeclaredField("uiManager");
        uiManagerField.setAccessible(true);
        UIElementManager uiManager = (UIElementManager) uiManagerField.get(GM);
        Field uiElementsField = UIElementManager.class.getDeclaredField("uiElements");
        uiElementsField.setAccessible(true);
        ((Map<UIElementName, Node>) uiElementsField.get(uiManager)).clear();
    }

    @Test
    @DisplayName("Test addUIElement with valid and invalid UI elements added")
    public void testAddUIElementWithValidAndInvalidUIElements() {
        GM.addUIElement(UIElementName.MIN_PRICE_SLIDER, new HBox());
        GM.addUIElement(UIElementName.SEARCH_FIELD, new TextField("Test Query"));
        assert GM.getSearchData().size()  == 1 : "Expected search data to contain 1 entry when only invalid UI elements are added, but got: " + GM.getSearchData();
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
        assert output.contains("Number of results: 39") : "Expected output to contain 'Number of results: 39', but got: " + output;
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
