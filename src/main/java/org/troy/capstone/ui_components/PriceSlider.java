package org.troy.capstone.ui_components;

import org.troy.capstone.constants.TestFXId;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

/**
 * The {@code PriceSlider} class represents a UI component that allows users to select a price range for filtering search results.
 */
public class PriceSlider extends VBox {

    /** The slider for selecting the minimum price in the range. */
    private Slider minSlider;
    /** The slider for selecting the maximum price in the range. */
    private Slider maxSlider;
    /** The label that displays the currently selected price range. */
    private final Label label;
    
    /** Constructor for {@code PriceSlider}. Initializes the sliders and label.
    *
     * @pre min should be less than max to ensure valid slider ranges.
     *
     * @param min The minimum price value for the sliders.
     * @param max The maximum price value for the sliders.
     */
    public PriceSlider(double min, double max) {

        minSlider = new Slider(min, max, min);
        minSlider.setId(TestFXId.MIN_PRICE_SLIDER.getId());
        minSlider.setShowTickLabels(true);
        minSlider.setShowTickMarks(true);
        minSlider.setMajorTickUnit((max - min) / 4);
        minSlider.setBlockIncrement(1);
        minSlider.setPrefWidth(150);
        
        maxSlider = new Slider(min, max, max);
        maxSlider.setId(TestFXId.MAX_PRICE_SLIDER.getId());
        maxSlider.setShowTickLabels(true);
        maxSlider.setShowTickMarks(true);
        maxSlider.setMajorTickUnit((max - min) / 4);
        maxSlider.setBlockIncrement(1);
        maxSlider.setPrefWidth(150);

        label = new Label(String.format("Price: $%.0f - $%.0f", min, max));
        label.setStyle("-fx-font-weight: bold;");
        
        //Update label when sliders change to be within $1 of each other to prevent crossing
        minSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double minVal = newVal.doubleValue();
            if (minVal + 1 > maxSlider.getValue())
                maxSlider.setValue(Math.min(minVal + 1, max)); //Ensure max slider is always at least $1 above min slider
            updateLabel();
        });
        maxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double maxVal = newVal.doubleValue();
            if (maxVal - 1 < minSlider.getValue())
                minSlider.setValue(Math.max(maxVal - 1, min)); //Ensure min slider is always at least $1 below max slider
            updateLabel();
        });
        
        this.getChildren().addAll(label, minSlider, maxSlider);
        this.setSpacing(10);
    }
    
    /** Updates the label to display the currently selected price range based on the values of the min and max sliders.
     * 
     * @post The label will be updated to reflect the current values of the min and max sliders, displaying the selected price range in a formatted manner.
     */
    private void updateLabel() {
        label.setText(String.format("Price: $%.0f - $%.0f", 
                                   minSlider.getValue(), 
                                   maxSlider.getValue()));
    }

    /** Gets the minimum price value selected by the user.
     *
     * @return The minimum price value.
     */
    public double getMin() {
        return minSlider.getValue();
    }

    /** Gets the maximum price value selected by the user.
     *
     * @return The maximum price value.
     */
    public double getMax() {
        return maxSlider.getValue();
    }
    
}
