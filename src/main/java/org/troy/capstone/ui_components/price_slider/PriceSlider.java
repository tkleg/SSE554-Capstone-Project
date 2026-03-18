package org.troy.capstone.ui_components.price_slider;

import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.managers.GeneralManager;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

/**
 * The PriceSlider class represents a UI component that allows users to select a price range for filtering search results.
 */
public class PriceSlider extends VBox {

    /** The slider for selecting the minimum price in the range. */
    private Slider minSlider;
    /** The slider for selecting the maximum price in the range. */
    private Slider maxSlider;
    /** The label that displays the currently selected price range. */
    private final Label label;
    
    /** Constructor for PriceSlider. Initializes the sliders and label, and registers them with the GeneralManager.
     * @pre <ul><li>min should be less than max to ensure valid slider ranges.</li>
     *      <li>The PriceSlider should be properly initialized to allow for user interaction with the sliders and display of the selected price range.</li>
     *      <li>generalManager should be properly initialized to allow for adding the created PriceSlider to it.</li></ul>
     *
     * @param min The minimum price value for the sliders.
     * @param max The maximum price value for the sliders.
     * @param generalManager The GeneralManager instance used to register the sliders.
     */
    public PriceSlider(double min, double max, GeneralManager generalManager) {

        // Min slider
        minSlider = new Slider(min, max, min);
        minSlider.setShowTickLabels(true);
        minSlider.setShowTickMarks(true);
        minSlider.setMajorTickUnit((max - min) / 4);
        minSlider.setBlockIncrement(1);
        minSlider.setPrefWidth(150);
        generalManager.addUIElement(UIElementName.MIN_PRICE_SLIDER, minSlider);

        // Max slider
        maxSlider = new Slider(min, max, max);
        maxSlider.setShowTickLabels(true);
        maxSlider.setShowTickMarks(true);
        maxSlider.setMajorTickUnit((max - min) / 4);
        maxSlider.setBlockIncrement(1);
        maxSlider.setPrefWidth(150);
        generalManager.addUIElement(UIElementName.MAX_PRICE_SLIDER, maxSlider);

        // Label
        label = new Label(String.format("Price: $%.0f - $%.0f", min, max));
        //Bold style for the label
        label.setStyle("-fx-font-weight: bold;");
        
        // Update label when sliders change to be within 1 dollar of each other to prevent crossing
        minSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double minVal = newVal.doubleValue();
            if (minVal + 1 > maxSlider.getValue())
                maxSlider.setValue(Math.min(minVal + 1, max)); //Ensure max slider is always at least 1 unit above min slider
            updateLabel();
        });
        maxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double maxVal = newVal.doubleValue();
            if (maxVal - 1 < minSlider.getValue())
                minSlider.setValue(Math.max(maxVal - 1, min)); //Ensure min slider is always at least 1 unit below max slider
            updateLabel();
        });
        
        this.getChildren().addAll(label, minSlider, maxSlider);
        this.setSpacing(10);
    }
    
    /** Updates the label to display the currently selected price range based on the values of the min and max sliders. */
    private void updateLabel() {
        label.setText(String.format("Price: $%.0f - $%.0f", 
                                   minSlider.getValue(), 
                                   maxSlider.getValue()));
    }
    
}
