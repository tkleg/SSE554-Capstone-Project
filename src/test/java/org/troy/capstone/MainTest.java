package org.troy.capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import org.troy.capstone.constants.TestFXId;
import org.troy.capstone.search_engine.sorting.comparator.RowComparator;
import org.troy.capstone.ui_components.filters.StarRatingFilter;
import org.troy.capstone.ui_components.filters.categorical.FilterPanel;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        new Main().start(stage);
    }


    @Test
    public void testApp() throws ReflectiveOperationException {
        //Assert that the stage is showing after setup
        assertTrue(stage.isShowing(), "Primary stage should be showing after setup");
        setSortOptionAndTest();
        setSearchQueryAndTestRetrieval();
        setPriceSliderAndTestRetrievals();
        setStarRatingAndTest();
        /*try {
            setCategoryDataAndTest();
            setPublisherDataAndTest();
            setTagDataAndTest();
        } catch (InterruptedException e) {
            System.err.println("Test was interrupted: " + e.getMessage());
        }*/
        //clickFirstNameLabelAndTest();
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
        double minPrice = 100.0;
        double maxPrice = 500.0;

        double pixelsPerDollar = minPriceSlider.getWidth() / (minPriceSlider.getMax() - minPriceSlider.getMin());

        double minOffset = Math.abs((minPrice - minPriceSlider.getValue()) * pixelsPerDollar);
        double maxOffset = Math.abs((maxPrice - maxPriceSlider.getValue()) * pixelsPerDollar);
        // Move min slider thumb to the right (simulate user drag)
        interact(() -> {
            drag(minPriceSlider.lookup(".thumb")).dropBy(minOffset, 0);
        });

        // Move max slider thumb to the left (simulate user drag)
        interact(() -> {
            drag(maxPriceSlider.lookup(".thumb")).dropBy(-maxOffset, 0);
        });

        // Wait for UI events to process
        WaitForAsyncUtils.waitForFxEvents();

        double actualMinPrice = minPriceSlider.getValue();
        double actualMaxPrice = maxPriceSlider.getValue();
        // Allow a wider delta due to drag precision
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

        //Click on the 3 star label
        interact(() -> clickOn(stars[2]) );
        //Check that the first 3 stars are filled and the 4th and 5th stars are not filled
        for (int i = 0; i < MAX_STARS; i++) {
            String expectedText = i <= 2 ? FILLED_STAR : EMPTY_STAR;
            String actualText = stars[i].getText();
            assertEquals(expectedText, actualText, "Star " + (i + 1) + " should be " + (i <= 2 ? "filled" : "empty") + " after clicking on star 3, but was " + (actualText.equals(FILLED_STAR) ? "filled" : actualText.equals(EMPTY_STAR) ? "empty" : "unknown"));
        }

        //Check the selected rating is 3
        actualSelectedRating = starRatingFilter.getSelectedRating();
        assertEquals(3, actualSelectedRating, "Selected star rating should be 3 after clicking on the 3rd star, but was " + actualSelectedRating);

    }

    public void setCategoryDataAndTest() throws InterruptedException {
        FilterPanel categoryPanel = TestUtils.lookupByTestFXId(TestFXId.FILTER_PANEL_PREFIX.getId() + "category");
        interact(() -> clickOn(categoryPanel));
        Thread.sleep(500);
        CheckBox electronicsCheckbox = TestUtils.lookupByTestFXId(TestFXId.CHECKBOX_PREFIX.getId() + "electronics");
        interact(() -> clickOn(electronicsCheckbox));
        Thread.sleep(500);
        interact(() -> categoryPanel.setExpanded(false) ); //Collapse the panel again
        Thread.sleep(500);
        assertTrue(electronicsCheckbox.isSelected(), "Checkbox for Electronics should be selected, but was not");
    }

    public void setPublisherDataAndTest() throws InterruptedException {
        FilterPanel publisherPanel = TestUtils.lookupByTestFXId(TestFXId.FILTER_PANEL_PREFIX.getId() + "publisher");
        interact(() -> clickOn(publisherPanel));
        Thread.sleep(500);
        CheckBox urbanNestCheckbox = TestUtils.lookupByTestFXId(TestFXId.CHECKBOX_PREFIX.getId() + "urbannest");
        interact(() -> clickOn(urbanNestCheckbox));
        Thread.sleep(500);
        interact(() -> publisherPanel.setExpanded(false) ); //Collapse the panel again
        Thread.sleep(500);
        assertTrue(urbanNestCheckbox.isSelected(), "Checkbox for UrbanNest should be selected, but was not");
    }

    public void setTagDataAndTest() throws InterruptedException {
        FilterPanel tagPanel = TestUtils.lookupByTestFXId(TestFXId.FILTER_PANEL_PREFIX.getId() + "tags");
        interact(() -> clickOn(tagPanel));
        Thread.sleep(1000);
        CheckBox smartHomeCheckbox = TestUtils.lookupByTestFXId(TestFXId.CHECKBOX_PREFIX.getId() + "bestseller");
        interact(() -> clickOn(smartHomeCheckbox));
        Thread.sleep(1000);
        interact(() -> tagPanel.setExpanded(false) ); //Collapse the panel again
        Thread.sleep(1000);
        assertTrue(smartHomeCheckbox.isSelected(), "Checkbox for Best Seller should be selected, but was not");
    }

    public void clickFirstNameLabelAndTest() {
        Label firstNameLabel = TestUtils.lookupByTestFXId(TestFXId.FIRST_SEARCHED_ITEM_NAME_LABEL);
        interact(() -> clickOn(firstNameLabel));
        assert true;//This test is testing that no errors are thrown
    }

}