package org.troy.capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.troy.capstone.constants.TestFXId;
import org.troy.capstone.search_engine.sorting.comparator.RowComparator;
import org.troy.capstone.ui_components.filters.StarRatingFilter;
import org.troy.capstone.ui_components.filters.categorical.FilterPanel;

import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class MainTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        new Main().start(stage);
    }


    @Test
    public void testApp() {
        //Assert that the stage is showing after setup
        assertTrue(stage.isShowing(), "Primary stage should be showing after setup");
        setSortOptionAndTest();
        setSearchQueryAndTestRetrieval();
        setPriceSliderAndTestRetrievals();
        setStarRatingAndTest();
        try {
            setCategoryDataAndTest();
            setPublisherDataAndTest();
            setTagDataAndTest();
        } catch (InterruptedException e) {
            System.err.println("Test was interrupted: " + e.getMessage());
        }
        clickFirstNameLabelAndTest();
    }

    public void setSortOptionAndTest() {
        ComboBox<RowComparator> dropdown = TestUtils.lookupByTestFXId(TestFXId.SORT_OPTION_DROPDOWN);
        interact(() -> {
            dropdown.show();
            dropdown.getSelectionModel().select(4);
        });
        RowComparator selected = dropdown.getSelectionModel().getSelectedItem();
        RowComparator expected = new RowComparator(RowComparator.SortType.RATING_ASCENDING);
        assertEquals(expected, selected, "Selected sorting option should be Rating Ascending, but was " + selected);
    }

    public void setSearchQueryAndTestRetrieval() {
        TextField searchField = TestUtils.lookupByTestFXId(TestFXId.SEARCH_FIELD);
        String query = "electric";
        interact(() -> searchField.setText(query));
        String actualQuery = searchField.getText();
        assertEquals(query, actualQuery, "Search query should be set to '" + query + "', but was '" + actualQuery + "'");
    }

    public void setPriceSliderAndTestRetrievals() {
        Slider minPriceSlider = TestUtils.lookupByTestFXId(TestFXId.MIN_PRICE_SLIDER);
        double minPrice = 100.0;
        interact(() -> minPriceSlider.setValue(minPrice));
        double actualMinPrice = minPriceSlider.getValue();
        assertEquals(minPrice, actualMinPrice, 0.01, "Min price slider should be set to " + minPrice + ", but was " + actualMinPrice);

        Slider maxPriceSlider = TestUtils.lookupByTestFXId(TestFXId.MAX_PRICE_SLIDER);
        double maxPrice = 500.0;
        interact(() -> maxPriceSlider.setValue(maxPrice));
        double actualMaxPrice = maxPriceSlider.getValue();
        assertEquals(maxPrice, actualMaxPrice, 0.01, "Max price slider should be set to " + maxPrice + ", but was " + actualMaxPrice);
    }

    public void setStarRatingAndTest() {
        int expectedRating = 4;
        Label star4 = TestUtils.lookupByTestFXId(TestFXId.STAR_LABEL_PREFIX.getId() + expectedRating);
        interact(() -> clickOn(star4));
        StarRatingFilter starRatingFilter = TestUtils.lookupByTestFXId(TestFXId.STAR_RATING_FILTER);
        int actualRating = starRatingFilter.getSelectedRating();
        assertEquals(expectedRating, actualRating, "Selected star rating should be " + expectedRating + ", but was " + actualRating);   
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
