package org.troy.capstone.managers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.data_structures.ItemTable.ItemHashMap;
import org.troy.capstone.ui_components.filters.categorical.FiltersContainer;
import org.troy.capstone.ui_components.filters.stars.StarRatingFilter;
import org.troy.capstone.ui_components.items.searched.SearchedItemPagination;
import org.troy.capstone.utils.TableUtils;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import tech.tablesaw.api.Table;

public class UIElementManagerTest {
    private UIElementManager uiElementManager;
    private static Table table;
    private static ItemHashMap itemHashMap;
    private static GeneralManager generalManager;
    private static FiltersContainer dummyFiltersContainer;

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setupAll() {
        // Initialize JavaFX environment
        new JFXPanel();
        
        // Now safely create JavaFX components
        table = TableUtils.readCleanedAttributedData();
        itemHashMap = ItemHashMap.fromTable(table);
        generalManager = new GeneralManager(table);
        dummyFiltersContainer = FiltersContainer.create(generalManager, itemHashMap);
    }

    @BeforeEach
    public void setup() {
        uiElementManager = new UIElementManager();
    }

    @Test
    @DisplayName("Test getElement with no data added")
    public void testGetElementWithNoData() {
        assert uiElementManager.getElement(UIElementName.MIN_PRICE_SLIDER).isEmpty() : "Expected empty Optional when no data is added, but got: " + uiElementManager.getElement(UIElementName.MIN_PRICE_SLIDER);
    }

    @Test
    @DisplayName("Test getElement with valid data added")
    public void testGetElementWithValidData() {
        uiElementManager.addElement(UIElementName.MIN_PRICE_SLIDER, new HBox());
        assert uiElementManager.getElement(UIElementName.MIN_PRICE_SLIDER).isPresent() : "Expected non-empty Optional when valid data is added, but got: " + uiElementManager.getElement(UIElementName.MIN_PRICE_SLIDER);
    }
    
    @Test
    @DisplayName("Test getSearchData with all proper UI elements added and properly cast")
    public void testGetSearchDataWithAllProperUIElementsProperlyCast() {
        uiElementManager.addElement(UIElementName.MIN_PRICE_SLIDER, new Slider(0, 100, 25));
        uiElementManager.addElement(UIElementName.MAX_PRICE_SLIDER, new Slider(0, 100, 75));
        uiElementManager.addElement(UIElementName.SEARCH_FIELD, new TextField("Test Query"));
        uiElementManager.addElement(UIElementName.FILTERS_CONTAINER, dummyFiltersContainer);
        uiElementManager.addElement(UIElementName.STAR_RATING_FILTER, StarRatingFilter.create(generalManager));

        assert uiElementManager.getSearchData().size() == 5 : "Expected search data to contain 5 entries when all proper UI elements are added and properly cast, but got: " + uiElementManager.getSearchData();
    }

    @Test
    @DisplayName("Test getSearchData with some proper UI elements added and properly cast")
    public void testGetSearchDataWithSomeProperUIElementsProperlyCast() {
        uiElementManager.addElement(UIElementName.MIN_PRICE_SLIDER, new Slider(0, 100, 25));
        uiElementManager.addElement(UIElementName.SEARCH_FIELD, new TextField("Test Query"));

        assert uiElementManager.getSearchData().size() == 2 : "Expected search data to contain 2 entries when some proper UI elements are added and properly cast, but got: " + uiElementManager.getSearchData();
    }

    @Test
    @DisplayName("Test getSearchData with all proper UI elements added but not properly cast")
    public void testGetSearchDataWithAllProperUIElementsNotProperlyCast() {
        uiElementManager.addElement(UIElementName.MIN_PRICE_SLIDER, new HBox());
        uiElementManager.addElement(UIElementName.MAX_PRICE_SLIDER, new HBox());
        uiElementManager.addElement(UIElementName.SEARCH_FIELD, new HBox());
        uiElementManager.addElement(UIElementName.FILTERS_CONTAINER, new HBox());
        uiElementManager.addElement(UIElementName.STAR_RATING_FILTER, new HBox());

        assert uiElementManager.getSearchData().isEmpty() : "Expected empty search data when all proper UI elements are added but not properly cast, but got: " + uiElementManager.getSearchData();
    }

    @Test
    @DisplayName("Test getSearchData with some proper UI elements added but not properly cast")
    public void testGetSearchDataWithSomeProperUIElementsNotProperlyCast() {
        uiElementManager.addElement(UIElementName.MIN_PRICE_SLIDER, new HBox());
        uiElementManager.addElement(UIElementName.SEARCH_FIELD, new TextField("Test Query"));

        assert uiElementManager.getSearchData().size() == 1 : "Expected search data to contain 1 entry when some proper UI elements are added but not properly cast, but got: " + uiElementManager.getSearchData();
    }

    @Test
    @DisplayName("Test getSearchData with no UI elements added")
    public void testGetSearchDataWithNoUIElements() {
        assert uiElementManager.getSearchData().isEmpty() : "Expected empty search data when no UI elements are added, but got: " + uiElementManager.getSearchData();
    }

    @Nested
    @DisplayName("Tests for updateSearchedItemPagination")
    @SuppressWarnings("unused")
    class UpdateSearchedItemPaginationTests {
        private PrintStream originalOut;
        private ByteArrayOutputStream outContent;

        @BeforeEach
        void setUp() {
            originalOut = System.out;
            outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));
        }

        @AfterEach
        void tearDown() {
            System.setOut(originalOut);
        }
        
        @Test
        @DisplayName("Test updateSearchedItemPagination with a pagination added to the manager")
        public void testUpdateSearchedItemPaginationWithPagination() {
            uiElementManager.addElement(UIElementName.SEARCHED_ITEM_PAGINATION, SearchedItemPagination.create(itemHashMap, generalManager));

            uiElementManager.updateSearchedItemPagination(List.of());

            String output = outContent.toString();

            assert !output.contains("Searched item pagination not found in UIElementManager, cannot update search results.") : "Expected no error message when a pagination is added to the manager, but got: " + output;

        }

        @Test
        @DisplayName("Test updateSearchedItemPagination with no pagination added to the manager")
        public void testUpdateSearchedItemPaginationWithNoPagination() {
            uiElementManager.updateSearchedItemPagination(List.of());

            String output = outContent.toString();

            assert output.contains("Searched item pagination not found in UIElementManager, cannot update search results.") : "Expected error message when no pagination is added to the manager, but got: " + output;

        }

        @Test
        @DisplayName("Test updateSearchedItemPagination with pagination added but not properly cast")
        public void testUpdateSearchedItemPaginationWithPaginationNotProperlyCast() {
            uiElementManager.addElement(UIElementName.SEARCHED_ITEM_PAGINATION, new HBox());

            uiElementManager.updateSearchedItemPagination(List.of());

            String output = outContent.toString();

            assert output.contains("Error retrieving searched item pagination value") : "Expected error message when pagination is not properly cast, but got: " + output;

        }
    }
}
