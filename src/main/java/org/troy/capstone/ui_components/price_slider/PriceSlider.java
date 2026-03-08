package org.troy.capstone.ui_components.price_slider;

import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.managers.GeneralManager;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class PriceSlider extends VBox {

    private Slider minSlider;
    private Slider maxSlider;
    private final Label label;
    
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
    
    private void updateLabel() {
        label.setText(String.format("Price: $%.0f - $%.0f", 
                                   minSlider.getValue(), 
                                   maxSlider.getValue()));
    }
    
}
