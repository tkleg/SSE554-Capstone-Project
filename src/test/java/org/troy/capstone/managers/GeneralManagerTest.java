package org.troy.capstone.managers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.constants.UIDataName;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.search_engine.sorting.Sorter;
import org.troy.capstone.ui_components.filters.StarRatingFilter;
import org.troy.capstone.ui_components.filters.categorical.FiltersContainer;
import org.troy.capstone.ui_components.items.RecentlyViewedWindow;
import org.troy.capstone.ui_components.items.SimilarItemsContainer;
import org.troy.capstone.ui_components.items.searched.SearchedItemPagination;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import tech.tablesaw.api.Table;

public class GeneralManagerTest {

    private static final Table table = TestDataHolder.getTableCopy();
    private static final ItemHashMap itemHashMap = TestDataHolder.getItemHashMapCopy();
    private static final GeneralManager GM = new GeneralManager(table, itemHashMap);
    private static final GeneralManager fullGM = new GeneralManager(table, itemHashMap);
    private static Button fullGMButton;

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() {
        new JFXPanel();

        fullGMButton = new Button("Search");
        fullGM.addUIElement(UIElementName.MIN_PRICE_SLIDER, new Slider(0, 100, 25));
        fullGM.addUIElement(UIElementName.MAX_PRICE_SLIDER, new Slider(0, 100, 75));
        fullGM.addUIElement(UIElementName.SEARCH_FIELD, new TextField("Test Query"));

        FiltersContainer filtersContainer = new FiltersContainer(TestDataHolder.getItemHashMapCopy().getItemsAsList());
        fullGM.addUIElement(UIElementName.FILTERS_CONTAINER, filtersContainer);
        fullGM.addUIElement(UIElementName.STAR_RATING_FILTER, new StarRatingFilter());
        fullGM.addUIElement(UIElementName.RECENTLY_VIEWED_WINDOW, RecentlyViewedWindow.create());
        fullGM.addUIElement(UIElementName.SEARCHED_ITEM_PAGINATION, new SearchedItemPagination(itemHashMap));
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
        Map<UIDataName, Object> uiElementManagerSearchData = (Map<UIDataName, Object>) uiElementsField.get(uiManager);
        uiElementManagerSearchData.clear();
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
    public void testPrintedResultsWithFullGMSetup() throws InterruptedException {
    
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            fullGMButton.fire();
            latch.countDown();
        });
        latch.await();

        String output = outContent.toString();
        System.out.println("Captured Output: " + output);
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
        System.out.println("Captured Output: " + output); //Print the captured output for debugging
        System.setOut(originalOut);

        assert output.contains("Search Data: {}") : "Expected output to contain 'Search Data: {}', but got: " + output;
        assert output.contains( "Number of results: 961" ) : "Expected output to contain 'Number of results: 961', but got: " + output;
    }

    @Test
    @DisplayName("Test readyToMakeRecentlyViewedManager with missing UI elements")
    public void testReadyToMakeRecentlyViewedManagerWithMissingUIElements() throws ReflectiveOperationException {
        GeneralManager gm = new GeneralManager(table, itemHashMap);
        Method methodField = GeneralManager.class.getDeclaredMethod("readyToMakeRecentlyViewedManager");
        methodField.setAccessible(true);
        boolean result = (boolean) methodField.invoke(gm);
        assert !result : "Expected readyToMakeRecentlyViewedManager to return false when required UI elements are missing, but got: " + result;
    }

    @Test
    @DisplayName("Test readyToMakeRecentlyViewedManager with all required UI elements")
    public void testReadyToMakeRecentlyViewedManagerWithAllRequiredUIElements() throws ReflectiveOperationException {
        Method methodField = GeneralManager.class.getDeclaredMethod("readyToMakeRecentlyViewedManager");
        methodField.setAccessible(true);
        boolean result = (boolean) methodField.invoke(fullGM);
        assert result : "Expected readyToMakeRecentlyViewedManager to return true when all required UI elements are present, but got: " + result;
    }

    @Test
    public void testfilterAndPrintNumberOfResultsWithInvalidSortingOption() throws Exception {
        Map<UIDataName, Object> fakeSearchData = new HashMap<>();
        fakeSearchData.put(UIDataName.SORTING_OPTION, "Invalid Comparator");

        //Spy on Sorter class to verify it is not called
        MockedStatic<Sorter> sorterClass = Mockito.mockStatic(Sorter.class);

        //Spy on GM to return our fakeSearchData
        GeneralManager spyGM = Mockito.spy(GM);
        Mockito.doReturn(fakeSearchData).when(spyGM).getSearchData();

        spyGM.filterAndPrintNumberOfResults();

        sorterClass.verify(() -> Sorter.sortTable(Mockito.any(), Mockito.any()), Mockito.never());
        sorterClass.close();
    }

    @Nested
    @DisplayName("Test addUIElement with the ready to make recently viewed manager condition")
    @SuppressWarnings("unused")
    class TestChecksforAddingRecentlyViewedManagerAndSimilarItemsManager {

        static Field recentlyViewedManagerCreatedField;
        static Field similarItemsManagerCreatedField;
        static GeneralManager spyGM;

        @BeforeAll
        public static void setup() throws ReflectiveOperationException {
            recentlyViewedManagerCreatedField = GeneralManager.class.getDeclaredField("recentlyViewedManagerCreated");
            recentlyViewedManagerCreatedField.setAccessible(true);
            similarItemsManagerCreatedField = GeneralManager.class.getDeclaredField("similarItemsManagerCreated");
            similarItemsManagerCreatedField.setAccessible(true);
            spyGM = Mockito.spy(new GeneralManager(table, itemHashMap));
        }

        @SuppressWarnings("unchecked")
        @BeforeEach
        public void resetRecentlyViewedManagerCreated() throws ReflectiveOperationException {
            recentlyViewedManagerCreatedField.set(spyGM, false);
            similarItemsManagerCreatedField.set(spyGM, false);
            Field uiManagerField = GeneralManager.class.getDeclaredField("uiManager");
            uiManagerField.setAccessible(true);
            UIElementManager uiManager = (UIElementManager) uiManagerField.get(spyGM);
            Field uiElementsField = UIElementManager.class.getDeclaredField("uiElements");
            uiElementsField.setAccessible(true);
            ((Map<UIElementName, Node>) uiElementsField.get(uiManager)).clear();
            Mockito.reset(spyGM);
        }

        @Test
        @DisplayName("Test addUIElement when neither required UI element for the RecentlyViewedManager is present")
        public void testAddUIElementWhenNeitherRequiredUIElementIsPresent() throws Exception {
            //Use Mockito to spy on GeneralManager
            spyGM.addUIElement(UIElementName.MIN_PRICE_SLIDER, new Slider());
            boolean recentlyViewedManagerCreated = (boolean) recentlyViewedManagerCreatedField.get(spyGM);
            assert !recentlyViewedManagerCreated : "Expected recentlyViewedManagerCreated to be false when required UI elements are missing, but got: " + recentlyViewedManagerCreated;
            //Verify readyToMakeRecentlyViewedManager is not called
            Mockito.verify(spyGM, Mockito.times(1)).readyToMakeRecentlyViewedManager();
        }

        @Test
        @DisplayName("Test addUIElement when just the pagination element is present for the RecentlyViewedManager")
        public void testAddUIElementWhenJustPaginationElementIsPresent() throws Exception {
            spyGM.addUIElement(UIElementName.SEARCHED_ITEM_PAGINATION, new SearchedItemPagination(itemHashMap));
            boolean recentlyViewedManagerCreated = (boolean) recentlyViewedManagerCreatedField.get(spyGM);
            assert !recentlyViewedManagerCreated : "Expected recentlyViewedManagerCreated to be false when one required UI element is missing, but got: " + recentlyViewedManagerCreated;
            //Verify readyToMakeRecentlyViewedManager is called
            Mockito.verify(spyGM, Mockito.times(1)).readyToMakeRecentlyViewedManager();
        }

        @Test
        @DisplayName("Test addUIElement when just the recently viewed window element is present for the RecentlyViewedManager")
        public void testAddUIElementWhenJustRecentlyViewedWindowElementIsPresent() throws Exception {
            spyGM.addUIElement(UIElementName.RECENTLY_VIEWED_WINDOW, RecentlyViewedWindow.create());
            boolean recentlyViewedManagerCreated = (boolean) recentlyViewedManagerCreatedField.get(spyGM);
            assert !recentlyViewedManagerCreated : "Expected recentlyViewedManagerCreated to be false when one required UI element is missing, but got: " + recentlyViewedManagerCreated;
            //Verify readyToMakeRecentlyViewedManager is called
            Mockito.verify(spyGM, Mockito.times(1)).readyToMakeRecentlyViewedManager();
        }

        @Test
        @DisplayName("Test addUIElement when both required UI elements are present for the RecentlyViewedManager")
        public void testAddUIElementWhenBothRequiredUIElementsArePresent() throws Exception {
            spyGM.addUIElement(UIElementName.SEARCHED_ITEM_PAGINATION, new SearchedItemPagination(itemHashMap));
            spyGM.addUIElement(UIElementName.RECENTLY_VIEWED_WINDOW, RecentlyViewedWindow.create());
            boolean recentlyViewedManagerCreated = (boolean) recentlyViewedManagerCreatedField.get(spyGM);
            assert recentlyViewedManagerCreated : "Expected recentlyViewedManagerCreated to be true when all required UI elements are present, but got: " + recentlyViewedManagerCreated;
            //Verify readyToMakeRecentlyViewedManager is called
            Mockito.verify(spyGM, Mockito.times(2)).readyToMakeRecentlyViewedManager();
        }

        @Test
        @DisplayName("Test addUIElement when the flag for recently viewed manager being created is already true")
        public void testAddUIElementWhenRecentlyViewedManagerAlreadyCreated() throws Exception {
            recentlyViewedManagerCreatedField.set(spyGM, true);
            spyGM.addUIElement(UIElementName.SEARCHED_ITEM_PAGINATION, new SearchedItemPagination(itemHashMap));
            spyGM.addUIElement(UIElementName.RECENTLY_VIEWED_WINDOW, RecentlyViewedWindow.create());
            boolean recentlyViewedManagerCreated = (boolean) recentlyViewedManagerCreatedField.get(spyGM);
            assert recentlyViewedManagerCreated : "Expected recentlyViewedManagerCreated to remain true when it is already true, but got: " + recentlyViewedManagerCreated;
            //Verify readyToMakeRecentlyViewedManager is called but the create method for RecentlyViewedManager is not called
            Mockito.verify(spyGM, Mockito.never()).readyToMakeRecentlyViewedManager();
        }

        @Test
        @DisplayName("Test addUIElement when neither required UI element for the SimilarItemsManager is present")
        public void testAddUIElementWhenNeitherRequiredUIElementForSimilarItemsManagerIsPresent() throws Exception {
            spyGM.addUIElement(UIElementName.MIN_PRICE_SLIDER, new Slider());
            boolean similarItemsManagerCreated = (boolean) similarItemsManagerCreatedField.get(spyGM);
            assert !similarItemsManagerCreated : "Expected similarItemsManagerCreated to be false when required UI elements are missing, but got: " + similarItemsManagerCreated;
            //Verify readyToMakeSimilarItemsManager is not called
            Mockito.verify(spyGM, Mockito.times(1)).readyToMakeSimilarItemsManager();
        }

        @Test
        @DisplayName("Test addUIElement when just the pagination element is present for the SimilarItemsManager")
        public void testAddUIElementWhenJustPaginationElementIsPresentForSimilarItemsManager() throws Exception {
            spyGM.addUIElement(UIElementName.SEARCHED_ITEM_PAGINATION, new SearchedItemPagination(itemHashMap));
            boolean similarItemsManagerCreated = (boolean) similarItemsManagerCreatedField.get(spyGM);
            assert !similarItemsManagerCreated : "Expected similarItemsManagerCreated to be false when one required UI element is missing, but got: " + similarItemsManagerCreated;
            //Verify readyToMakeSimilarItemsManager is called
            Mockito.verify(spyGM, Mockito.times(1)).readyToMakeSimilarItemsManager();
        }

        @Test
        @DisplayName("Test addUIElement when just the similar items container element is present for the SimilarItemsManager")
        public void testAddUIElementWhenJustSimilarItemsContainerElementIsPresentForSimilarItemsManager() throws Exception {
            spyGM.addUIElement(UIElementName.SIMILAR_ITEMS_CONTAINER, SimilarItemsContainer.create());
            boolean similarItemsManagerCreated = (boolean) similarItemsManagerCreatedField.get(spyGM);
            assert !similarItemsManagerCreated : "Expected similarItemsManagerCreated to be false when one required UI element is missing, but got: " + similarItemsManagerCreated;
            //Verify readyToMakeSimilarItemsManager is called
            Mockito.verify(spyGM, Mockito.times(1)).readyToMakeSimilarItemsManager();
        }

        @Test
        @DisplayName("Test addUIElement when both required UI elements are present for the SimilarItemsManager")
        public void testAddUIElementWhenBothRequiredUIElementsArePresentForSimilarItemsManager() throws Exception {
            spyGM.addUIElement(UIElementName.SEARCHED_ITEM_PAGINATION, new SearchedItemPagination(itemHashMap));
            spyGM.addUIElement(UIElementName.SIMILAR_ITEMS_CONTAINER, SimilarItemsContainer.create());
            boolean similarItemsManagerCreated = (boolean) similarItemsManagerCreatedField.get(spyGM);
            assert similarItemsManagerCreated : "Expected similarItemsManagerCreated to be true when all required UI elements are present, but got: " + similarItemsManagerCreated;
            //Verify readyToMakeSimilarItemsManager is called
            Mockito.verify(spyGM, Mockito.times(2)).readyToMakeSimilarItemsManager();
        }

        @Test
        @DisplayName("Test addUIElement when the flag for similar items manager being created is already true")
        public void testAddUIElementWhenSimilarItemsManagerAlreadyCreated() throws Exception {
            similarItemsManagerCreatedField.set(spyGM, true);
            spyGM.addUIElement(UIElementName.SEARCHED_ITEM_PAGINATION, new SearchedItemPagination(itemHashMap));
            spyGM.addUIElement(UIElementName.SIMILAR_ITEMS_CONTAINER, SimilarItemsContainer.create());
            boolean similarItemsManagerCreated = (boolean) similarItemsManagerCreatedField.get(spyGM);
            assert similarItemsManagerCreated : "Expected similarItemsManagerCreated to remain true when it is already true, but got: " + similarItemsManagerCreated;
            //Verify readyToMakeSimilarItemsManager is called but the create method for SimilarItemsManager is not called
            Mockito.verify(spyGM, Mockito.never()).readyToMakeSimilarItemsManager();
        }
    }
}
