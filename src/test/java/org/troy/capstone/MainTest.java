package org.troy.capstone;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import org.troy.capstone.constants.TestFXId;
import org.troy.capstone.search_engine.sorting.comparator.RowComparator;
import org.troy.capstone.ui_components.filters.StarRatingFilter;
import org.troy.capstone.ui_components.filters.categorical.FilterPanel;
import org.troy.capstone.ui_components.items.searched.SearchedItemPagination;
import org.troy.capstone.ui_components.items.SearchedItemPanel;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class MainTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
    }

    @BeforeEach
    public void setUp() throws Exception {
        SearchedItemPanel.firstInstanceMade = false;
        Config.graphBuildingEnabled = false;
    }

    @Test
    public void testDisplayInSimilarItemsAndRecentlyViewedItems()throws Exception{
        
        //Start test and get the triggering label
        Config.graphBuildingEnabled = true;
        ApplicationTest.launch(Main.class);
        Label nameLabel = TestUtils.lookupByTestFXId(TestFXId.FIRST_SEARCHED_ITEM_NAME_LABEL);
        assertNotNull(nameLabel, "First searched item name label should be found by TestFXId in testDisplayInSimilarItemsAndRecentlyViewedItems");

        //Test RecentlyViewedWindow before the click
        VBox recentlyViewedContent = TestUtils.lookupByTestFXId(TestFXId.RECENTLY_VIEWED_CONTAINER);
        int initialRecentlyViewedCount = recentlyViewedContent.getChildren().size();
        assertEquals(1, initialRecentlyViewedCount, "There should be 1 item (label) in the recently viewed container initially, but was " + initialRecentlyViewedCount);
        Node firstChild = recentlyViewedContent.getChildren().get(0);
        assertTrue(firstChild instanceof Label, "The first child in the recently viewed container should be a Label representing the container, but was " + firstChild.getClass().getSimpleName());

        //Test SimilarItemsWindow before the click
        HBox similarItemsContainer = TestUtils.lookupByTestFXId(TestFXId.SIMILAR_ITEMS_CONTAINER);
        int similarItemsCount = similarItemsContainer.getChildren().size();
        assertEquals(1, similarItemsCount, "There should be 1 item in the similar items container after clicking on the first searched item, but was " + similarItemsCount);
        Node similarFirstChild = similarItemsContainer.getChildren().get(0);
        assertTrue(similarFirstChild instanceof Label, "The first child in the similar items container should be a Label representing the container, but was " + similarFirstChild.getClass().getSimpleName());

        interact(() -> clickOn(nameLabel));

        //Test RecentlyViewedWindow after the click
        initialRecentlyViewedCount = recentlyViewedContent.getChildren().size();
        assertEquals(1, initialRecentlyViewedCount, "There should be 1 item in the recently viewed container after clicking on the first searched item, but was " + initialRecentlyViewedCount);
        firstChild = recentlyViewedContent.getChildren().get(0);
        assertTrue(firstChild instanceof SearchedItemPanel, "The first child in the recently viewed container should be a SearchedItemPanel representing the item, but was " + firstChild.getClass().getSimpleName());
    
        //Test SimilarItemsWindow after the click
        similarItemsCount = similarItemsContainer.getChildren().size();
        assertEquals(10, similarItemsCount, "There should be 10 items in the similar items container after clicking on the first searched item, but was " + similarItemsCount);
        for(int x = 0; x < 10; x++){
            Node child = similarItemsContainer.getChildren().get(x);
            assertTrue(child instanceof VBox, "Child " + (x+1) + " in the similar items container should be a VBox representing a similar item, but was " + child.getClass().getSimpleName());
        }
    }


    @Test
    public void testFilteredSearch() throws Exception {
        ApplicationTest.launch(Main.class);
        //Assert that the stage is showing after setup
        assertTrue(stage.isShowing(), "Primary stage should be showing after setup");
        setSortOptionAndTest();
        setSearchQueryAndTestRetrieval();
        setPriceSliderAndTestRetrievals();
        setStarRatingAndTest();
        setCategoryDataAndTest();
        setPublisherDataAndTest();
        setTagDataAndTest();
        clickSearchButtonTest();
    }

    public void clickSearchButtonTest() {
        Button searchButton = TestUtils.lookupByTestFXId(TestFXId.SEARCH_BUTTON);
        assertNotNull(searchButton, "Search button should be found by TestFXId in clickSearchButtonTest before clicking");
        interact(() -> clickOn(searchButton));
        WaitForAsyncUtils.waitForFxEvents();

        VBox resultsContainer = TestUtils.lookupByTestFXId(TestFXId.SEARCHED_ITEM_CONTAINER_CONTAINER);
        assertNotNull(resultsContainer, "Results container should be found by TestFXId in clickSearchButtonTest after clicking search button");

        int numResults = resultsContainer.getChildren().size();
        assertEquals(5, numResults);

        SearchedItemPagination pagination = TestUtils.lookupByTestFXId(TestFXId.SEARCHED_ITEM_PAGINATION);
        assertNotNull(pagination, "Pagination should be found by TestFXId in clickSearchButtonTest after clicking search button");

        int pageCount = pagination.getPageCount();
        assertEquals(1, pageCount, "There should only be 1 page of results for the search query in clickSearchButtonTest, but was " + pageCount);
    }

    public void setSortOptionAndTest() {
        ComboBox<RowComparator> dropdown = TestUtils.lookupByTestFXId(TestFXId.SORT_OPTION_DROPDOWN);

        interact(() -> clickOn(dropdown) );

        //Wait for the dropdown to open and populate
        WaitForAsyncUtils.waitForFxEvents();

        //Click on the "Rating Ascending" option
        RowComparator expectedOption = new RowComparator(RowComparator.SortType.RATING_ASCENDING);
        String optionId = TestFXId.SORT_OPTION_CELL_PREFIX.getId() + expectedOption.toString().replaceAll("\\s+", "_").toLowerCase();
        Node ratingAscOption = TestUtils.lookupByTestFXId(optionId);
        interact(() -> clickOn(ratingAscOption) );

        RowComparator selected = dropdown.getSelectionModel().getSelectedItem();
        assertEquals(expectedOption, selected, "Selected sorting option should be Rating Ascending, but was " + selected);
    }

    public void setSearchQueryAndTestRetrieval() {
        TextField searchField = TestUtils.lookupByTestFXId(TestFXId.SEARCH_FIELD);
        String query = "electric";
        interact(() -> clickOn(searchField) );
        interact(() -> eraseText(searchField.getText().length()) );
        interact(() -> write(query));
        WaitForAsyncUtils.waitForFxEvents();
        String actualQuery = searchField.getText();
        assertEquals(query, actualQuery, "Search query should be set to '" + query + "', but was '" + actualQuery + "'");
    }

    public void setPriceSliderAndTestRetrievals() {
        Slider minPriceSlider = TestUtils.lookupByTestFXId(TestFXId.MIN_PRICE_SLIDER);
        Slider maxPriceSlider = TestUtils.lookupByTestFXId(TestFXId.MAX_PRICE_SLIDER);
        double minPrice = 64;
        double maxPrice = 657;

        double pixelsPerDollar = minPriceSlider.getWidth() / (minPriceSlider.getMax() - minPriceSlider.getMin());

        double minOffset = Math.abs((minPrice - minPriceSlider.getValue()) * pixelsPerDollar);
        double maxOffset = Math.abs((maxPrice - maxPriceSlider.getValue()) * pixelsPerDollar);
        //Move min slider thumb to the right (simulate user drag)
        interact(() -> {
            drag(minPriceSlider.lookup(".thumb")).dropBy(minOffset, 0);
        });

        //Move max slider thumb to the left (simulate user drag)
        interact(() -> {
            drag(maxPriceSlider.lookup(".thumb")).dropBy(-maxOffset, 0);
        });

        //Wait for UI events to process
        WaitForAsyncUtils.waitForFxEvents();

        double actualMinPrice = minPriceSlider.getValue();
        double actualMaxPrice = maxPriceSlider.getValue();
        //Allow a wider delta due to drag precision
        System.out.println("Expected min price: " + minPrice + ", Actual min price: " + actualMinPrice);
        System.out.println("Expected max price: " + maxPrice + ", Actual max price: " + actualMaxPrice);
        assertTrue(Math.abs(actualMinPrice - minPrice) < 15, "Min price slider should be near " + minPrice + ", but was " + actualMinPrice);
        assertTrue(Math.abs(actualMaxPrice - maxPrice) < 15, "Max price slider should be near " + maxPrice + ", but was " + actualMaxPrice);
    
        //The FXRobot drag is not too precise, so the sliders are manually set later to ensure the correct values are set for later tests that rely on the slider values
        interact(() -> {
            minPriceSlider.setValue(minPrice);
            maxPriceSlider.setValue(maxPrice);
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(minPrice, minPriceSlider.getValue(), "Min price slider should be set to " + minPrice + " after manual set, but was " + minPriceSlider.getValue());
        assertEquals(maxPrice, maxPriceSlider.getValue(), "Max price slider should be set to " + maxPrice + " after manual set, but was " + maxPriceSlider.getValue());
    }

    public void setStarRatingAndTest() throws ReflectiveOperationException {
        Field MAX_STARS_FIELD = StarRatingFilter.class.getDeclaredField("MAX_STARS");
        MAX_STARS_FIELD.setAccessible(true);
        final int MAX_STARS = (int) MAX_STARS_FIELD.get(null);

        Field EMPTY_STAR_FIELD = StarRatingFilter.class.getDeclaredField("EMPTY_STAR");
        EMPTY_STAR_FIELD.setAccessible(true);
        String EMPTY_STAR = (String) EMPTY_STAR_FIELD.get(null);

        Field FILLED_STAR_FIELD = StarRatingFilter.class.getDeclaredField("FILLED_STAR");
        FILLED_STAR_FIELD.setAccessible(true);
        String FILLED_STAR = (String) FILLED_STAR_FIELD.get(null);

        Label[] stars = new Label[MAX_STARS];
        for (int i = 0; i < MAX_STARS; i++) {
            String starLabelId = TestFXId.STAR_LABEL_PREFIX.getId() + (i + 1);
            stars[i] = TestUtils.lookupByTestFXId(starLabelId);
        }

        StarRatingFilter starRatingFilter = TestUtils.lookupByTestFXId(TestFXId.STAR_RATING_FILTER);

        //Click on the 4 star label
        interact(() -> clickOn(stars[3]) );
        //Check that the first 4 stars are filled and the 5th star is not filled
        for (int i = 0; i < MAX_STARS; i++) {
            String expectedText = i <= 3 ? FILLED_STAR : EMPTY_STAR;
            String actualText = stars[i].getText();
            assertEquals(expectedText, actualText, "Star " + (i + 1) + " should be " + (i <= 3 ? "filled" : "empty") + " after clicking on star 4, but was " + (actualText.equals(FILLED_STAR) ? "filled" : actualText.equals(EMPTY_STAR) ? "empty" : "unknown"));
        }
        //Check the selected rating is 4
        int actualSelectedRating = starRatingFilter.getSelectedRating();
        assertEquals(4, actualSelectedRating, "Selected star rating should be 4 after clicking on the 4th star, but was " + actualSelectedRating);

        //Click on the 4 star label again to deselect
        interact(() -> clickOn(stars[3]) );
        //Check that all stars are now empty
        for (int i = 0; i < MAX_STARS; i++) {
            String actualText = stars[i].getText();
            assertEquals(EMPTY_STAR, actualText, "Star " + (i + 1) + " should be empty after clicking on star 4 again to deselect, but was " + (actualText.equals(FILLED_STAR) ? "filled" : actualText.equals(EMPTY_STAR) ? "empty" : "unknown"));
        }
        //Check the selected rating is 0 (no rating)
        actualSelectedRating = starRatingFilter.getSelectedRating();
        assertEquals(0, actualSelectedRating, "Selected star rating should be 0 after deselecting the 4th star, but was " + actualSelectedRating);

        //Click on the 2 star label
        interact(() -> clickOn(stars[1]) );
        //Check that the first 2 stars are filled and the 3rd, 4th, and 5th stars are not filled
        for (int i = 0; i < MAX_STARS; i++) {
            String expectedText = i <= 1 ? FILLED_STAR : EMPTY_STAR;
            String actualText = stars[i].getText();
            assertEquals(expectedText, actualText, "Star " + (i + 1) + " should be " + (i <= 1 ? "filled" : "empty") + " after clicking on star 2, but was " + (actualText.equals(FILLED_STAR) ? "filled" : actualText.equals(EMPTY_STAR) ? "empty" : "unknown"));
        }

        //Check the selected rating is 2
        actualSelectedRating = starRatingFilter.getSelectedRating();
        assertEquals(2, actualSelectedRating, "Selected star rating should be 2 after clicking on the 2nd star, but was " + actualSelectedRating);

    }

    public void setCategoryDataAndTest(){
        Set<String> expectedInitialSelectedOptions = Set.of("Clothing", "Electronics", "Sports & Outdoors", "Books", "Office Supplies");
        //Due to hidden field with the scrollpane, the checkbox is selected with the setting instead of a proper click event
        for (String category : expectedInitialSelectedOptions) {
            CheckBox categoryCheckbox = TestUtils.lookupByTestFXId(TestFXId.CHECKBOX_PREFIX.getId() + category.toLowerCase().replaceAll("\\s+", "_").replaceAll("\\.", ""));
            interact(() -> categoryCheckbox.setSelected(true));
            assertNotNull(categoryCheckbox, "Category checkbox for " + category + " should be found by TestFXId in setCategoryDataAndTest");
            assertTrue(categoryCheckbox.isSelected(), "Category checkbox for " + category + " should be initially selected in setCategoryDataAndTest");
        }
        
        FilterPanel categoryPanel = TestUtils.lookupByTestFXId(TestFXId.FILTER_PANEL_PREFIX.getId() + "category");
        Set<String> actualSelectedOptions = categoryPanel.getCheckedOptions();
        assertEquals(expectedInitialSelectedOptions, actualSelectedOptions, "Selected category options should match the expected initial selected options, but was " + actualSelectedOptions);
    }

    public void setPublisherDataAndTest() {
        Set<String> expectedInitialSelectedOptions = Set.of("Summit Gear Co.", "BrightLeaf Publishing", "Maple Street Press", "SilverLine Electronics");
        for (String publisher : expectedInitialSelectedOptions) {
            CheckBox publisherCheckbox = TestUtils.lookupByTestFXId(TestFXId.CHECKBOX_PREFIX.getId() + publisher.toLowerCase().replaceAll("\\s+", "_").replaceAll("\\.", ""));
            interact(() -> publisherCheckbox.setSelected(true));
            assertNotNull(publisherCheckbox, "Publisher checkbox for " + publisher + " should be found by TestFXId in setPublisherDataAndTest");
            assertTrue(publisherCheckbox.isSelected(), "Publisher checkbox for " + publisher + " should be initially selected in setPublisherDataAndTest");
        }

        FilterPanel publisherPanel = TestUtils.lookupByTestFXId(TestFXId.FILTER_PANEL_PREFIX.getId() + "publisher");
        Set<String> actualSelectedOptions = publisherPanel.getCheckedOptions();
        assertEquals(expectedInitialSelectedOptions, actualSelectedOptions, "Selected publisher options should match the expected initial selected options, but was " + actualSelectedOptions);
    }

    public void setTagDataAndTest(){
        Set<String> expectedInitialSelectedOptions = Set.of("Bestseller");
        for (String tag : expectedInitialSelectedOptions) {
            CheckBox tagCheckbox = TestUtils.lookupByTestFXId(TestFXId.CHECKBOX_PREFIX.getId() + tag.toLowerCase().replaceAll("\\s+", "_").replaceAll("\\.", ""));
            interact(() -> tagCheckbox.setSelected(true));
            assertNotNull(tagCheckbox, "Tag checkbox for " + tag + " should be found by TestFXId in setTagDataAndTest");
            assertTrue(tagCheckbox.isSelected(), "Tag checkbox for " + tag + " should be initially selected in setTagDataAndTest");
        }

        FilterPanel tagPanel = TestUtils.lookupByTestFXId(TestFXId.FILTER_PANEL_PREFIX.getId() + "tags");
        Set<String> actualSelectedOptions = tagPanel.getCheckedOptions();
        assertEquals(expectedInitialSelectedOptions, actualSelectedOptions, "Selected tag options should match the expected initial selected options, but was " + actualSelectedOptions);
    }

    public void clickFirstNameLabelAndTest() {
        Label firstNameLabel = TestUtils.lookupByTestFXId(TestFXId.FIRST_SEARCHED_ITEM_NAME_LABEL);
        interact(() -> clickOn(firstNameLabel));
        assert true;//This test is testing that no errors are thrown
    }

}