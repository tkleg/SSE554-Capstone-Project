package org.troy.capstone.ui_components.price_slider;

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
import javafx.scene.control.Slider;
import tech.tablesaw.api.Table;

public class PriceSliderTest {
    private static GeneralManager generalManager;
    private static Table table;
    private PriceSlider priceSlider;
    private static final double MIN_PRICE = 0;
    private static final double MAX_PRICE = 1000;

    @BeforeAll
    public static void setup() {
        new JFXPanel();
        table = TableUtils.readCleanedAttributedData();
        generalManager = new GeneralManager(table);
    }

    @BeforeEach
    public void setUp() {
        priceSlider = new PriceSlider(MIN_PRICE, MAX_PRICE, generalManager);
    }
    
    @Test
    @DisplayName("Test PriceSlider creation, and initial data")
    public void testPriceSliderCreation() {
        assertNotNull(priceSlider, "PriceSlider should be created successfully");
        assertNotNull(priceSlider.getMinSlider(), "Min slider should be initialized");
        assertNotNull(priceSlider.getMaxSlider(), "Max slider should be initialized");
        assertEquals(MIN_PRICE, priceSlider.getLowValue(), "Min slider should be initialized to min price");
        assertEquals(MAX_PRICE, priceSlider.getHighValue(), "Max slider should be initialized to max price");
        assertEquals("Price: $0 - $1000", priceSlider.getLabelText(), "Label should display the correct initial price range");
    }

    @Test
    @DisplayName("Test setting min slider and max slider to values not crossing each other")
    public void testPriceSliderMinMaxNotCrossing() {
        priceSlider.getMinSlider().setValue(200);
        assertEquals(200, priceSlider.getLowValue(), "Min slider should update to 200");
        assertEquals(MAX_PRICE, priceSlider.getHighValue(), "Max slider should remain at max price when min slider is set below it");

        priceSlider.getMaxSlider().setValue(800);
        assertEquals(800, priceSlider.getHighValue(), "Max slider should update to 800");
        assertEquals(200, priceSlider.getLowValue(), "Min slider should remain at 200 when max slider is set above it");
    
        assertEquals("Price: $200 - $800", priceSlider.getLabelText(), "Label should update to reflect new price range");
    }

    @Test
    @DisplayName("Test setting min slider above maxSlider value")
    public void testPriceSliderMinAboveMax() {
        priceSlider.getMaxSlider().setValue(500);
        //Should trigger max slider to change to 501 to maintain 1 dollar gap
        priceSlider.getMinSlider().setValue(900);
        
        assertEquals(900, priceSlider.getLowValue(), "Min slider should update to 900");
        assertEquals(901, priceSlider.getHighValue(), "Max slider should automatically adjust to maintain at least 1 unit above min slider");
        assertEquals("Price: $900 - $901", priceSlider.getLabelText(), "Label should update to reflect new price range");
    }

    @Test
    @DisplayName("Test setting max slider below minSlider value")
    public void testPriceSliderMaxBelowMin() {
        priceSlider.getMinSlider().setValue(400);
        //Should trigger min slider to change to 399 to maintain 1 dollar gap
        priceSlider.getMaxSlider().setValue(300);
        
        assertEquals(300, priceSlider.getHighValue(), "Max slider should update to 300");
        assertEquals(299, priceSlider.getLowValue(), "Min slider should automatically adjust to maintain at least 1 unit below max slider");
        assertEquals("Price: $299 - $300", priceSlider.getLabelText(), "Label should update to reflect new price range");
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
