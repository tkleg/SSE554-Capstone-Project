package org.troy.capstone.ui_components;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.managers.GeneralManager;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class PriceSliderTest {
    private static GeneralManager generalManager;
    private PriceSlider priceSlider;
    private static final double MIN_PRICE = 0;
    private static final double MAX_PRICE = 1000;
    private Field minSliderField, maxSliderField, labelField;
    private Slider minSlider, maxSlider;
    private double minSliderValue, maxSliderValue;
    private Label label;

    private void recalculatePriceSliderValues() {
        try {
            minSliderValue = minSlider.getValue();
            maxSliderValue = maxSlider.getValue();
        } catch (Exception e) {
            throw new RuntimeException("Failed to recalculate slider values", e);
        }
    }

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() {
        new JFXPanel();
        generalManager = new GeneralManager(TestDataHolder.getTableCopy());
    }

    @BeforeEach
    public void setUp() {
        priceSlider = new PriceSlider(MIN_PRICE, MAX_PRICE, generalManager);
        try {
            minSliderField = PriceSlider.class.getDeclaredField("minSlider");
            minSliderField.setAccessible(true);
            Object minSliderObj = minSliderField.get(priceSlider);
            assert minSliderObj instanceof Slider : "Expected minSlider to be an instance of Slider, but got: " + minSliderObj.getClass();
            minSlider = (Slider) minSliderObj;
            minSliderValue = minSlider.getValue();

            maxSliderField = PriceSlider.class.getDeclaredField("maxSlider");
            maxSliderField.setAccessible(true);
            Object maxSliderObj = maxSliderField.get(priceSlider);
            assert maxSliderObj instanceof Slider : "Expected maxSlider to be an instance of Slider, but got: " + maxSliderObj.getClass();
            maxSlider = (Slider) maxSliderObj;
            maxSliderValue = maxSlider.getValue();

            labelField = PriceSlider.class.getDeclaredField("label");
            labelField.setAccessible(true);
            Object labelObj = labelField.get(priceSlider);
            assert labelObj instanceof Label : "Expected label to be an instance of Label, but got: " + labelObj.getClass();
            label = (Label) labelObj;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access minSlider or maxSlider field via reflection", e);
        }
    }
    
    @Test
    @DisplayName("Test PriceSlider creation, and initial data")
    public void testPriceSliderCreation() {
        assertNotNull(priceSlider, "PriceSlider should be created successfully");
        assertNotNull(minSlider, "Min slider should be initialized");
        assertNotNull(maxSlider, "Max slider should be initialized");
        assertEquals(MIN_PRICE, minSliderValue, "Min slider should be initialized to min price");
        assertEquals(MAX_PRICE, maxSliderValue, "Max slider should be initialized to max price");
        assertEquals("Price: $0 - $1000", label.getText(), "Label should display the correct initial price range");
    }

    @Test
    @DisplayName("Test setting min slider and max slider to values not crossing each other")
    public void testPriceSliderMinMaxNotCrossing() {
        minSlider.setValue(200);
        recalculatePriceSliderValues();
        assertEquals(200, minSliderValue, "Min slider should update to 200");
        assertEquals(MAX_PRICE, maxSliderValue, "Max slider should remain at max price when min slider is set below it");

        maxSlider.setValue(800);
        recalculatePriceSliderValues();
        assertEquals(800, maxSliderValue, "Max slider should update to 800");
        assertEquals(200, minSliderValue, "Min slider should remain at 200 when max slider is set above it");
    
        assertEquals("Price: $200 - $800", label.getText(), "Label should update to reflect new price range");
    }

    @Test
    @DisplayName("Test setting min slider above maxSlider value")
    public void testPriceSliderMinAboveMax() {
        maxSlider.setValue(500);
        //Should trigger max slider to change to 501 to maintain 1 dollar gap
        minSlider.setValue(900);
        recalculatePriceSliderValues();
        assertEquals(900, minSliderValue, "Min slider should update to 900");
        assertEquals(901, maxSliderValue, "Max slider should automatically adjust to maintain at least 1 unit above min slider");
        assertEquals("Price: $900 - $901", label.getText(), "Label should update to reflect new price range");
    }

    @Test
    @DisplayName("Test setting max slider below minSlider value")
    public void testPriceSliderMaxBelowMin() {
        minSlider.setValue(400);
        //Should trigger min slider to change to 399 to maintain 1 dollar gap
        maxSlider.setValue(300);
        recalculatePriceSliderValues();
        
        assertEquals(300, maxSliderValue, "Max slider should update to 300");
        assertEquals(299, minSliderValue, "Min slider should automatically adjust to maintain at least 1 unit below max slider");
        assertEquals("Price: $299 - $300", label.getText(), "Label should update to reflect new price range");
    }

    @Test
    @DisplayName("Test that generalManager has the price sliders and they are cast properly")
    public void testPriceSlidersInGeneralManager(){
        Optional<Node> minSliderNode = generalManager.getUIElement(UIElementName.MIN_PRICE_SLIDER);
        Optional<Node> maxSliderNode = generalManager.getUIElement(UIElementName.MAX_PRICE_SLIDER);

        assert minSliderNode.isPresent() : "Expected generalManager to have a MIN_PRICE_SLIDER element, but it was not found.";
        assert minSliderNode.get() instanceof Slider : "Expected MIN_PRICE_SLIDER element to be an instance of Slider, but got: " + minSliderNode.get().getClass();
        assert maxSliderNode.isPresent() : "Expected generalManager to have a MAX_PRICE_SLIDER element, but it was not found.";
        assert maxSliderNode.get() instanceof Slider : "Expected MAX_PRICE_SLIDER element to be an instance of Slider, but got: " + maxSliderNode.get().getClass();
    }
}
