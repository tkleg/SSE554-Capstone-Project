package org.troy.capstone.ui_components.priceSlider;

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
        label = new Label("Price: $" + String.format("%.0f - %.0f", min, max));
        //Bold style for the label
        label.setStyle("-fx-font-weight: bold;");
        
        // Update label when sliders change
        minSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double minVal = newVal.doubleValue();
            if (minVal > maxSlider.getValue())
                maxSlider.setValue(minVal);
            updateLabel();
        });

        maxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double maxVal = newVal.doubleValue();
            if (maxVal < minSlider.getValue())
                minSlider.setValue(maxVal);
            updateLabel();
        });

        /*// Layout
        VBox sliderBox = new VBox(5);
        sliderBox.getChildren().addAll(
            new Label("Min: "), minSlider,
            new Label("Max: "), maxSlider
        );*///Removed extra labels for change in layout design
        
        this.getChildren().addAll(label, minSlider, maxSlider);
        this.setSpacing(10);
    }
    
    private void updateLabel() {
        label.setText(String.format("Price: $%.0f - $%.0f", 
                                   minSlider.getValue(), 
                                   maxSlider.getValue()));
    }
    
    public double getLowValue() {
        return minSlider.getValue();
    }
    
    public double getHighValue() {
        return maxSlider.getValue();
    }
}
