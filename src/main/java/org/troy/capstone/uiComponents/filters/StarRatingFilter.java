package org.troy.capstone.uiComponents.filters;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class StarRatingFilter extends HBox {
    private static final int MAX_STARS = 5;
    private static final String FILLED_STAR = "★";
    private static final String EMPTY_STAR = "☆";
    
    private final Label[] stars;
    private int selectedRating = 0;
    
    public StarRatingFilter() {
        stars = new Label[MAX_STARS];
        
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(2);
        
        //Create the star labels
        for (int i = 0; i < MAX_STARS; i++) {
            Label star = new Label(EMPTY_STAR);
            star.setFont(Font.font("Arial", 20));
            star.setTextFill(Color.GRAY);
            
            //Make stars clickable
            int starIndex = i + 1;
            star.setOnMouseClicked(e -> setRating(starIndex));
            //Start hover
            star.setOnMouseEntered(e -> previewRating(starIndex));
            //End hover
            star.setOnMouseExited(e -> updateDisplay());
            
            stars[i] = star;
            getChildren().add(star);
        }
        
        updateDisplay();
    }
    
    /**
     * Sets the minimum rating (1-5). Use 0 to clear the filter.
     */
    public void setRating(int rating) {
        // If clicking the same star that's already selected, clear the rating
        if (selectedRating == rating)
            return;
        else
            selectedRating = rating;
        
        updateDisplay();
 
    }
    
    /**
     * Gets the current minimum rating (0-5, where 0 means no filter)
     */
    public int getRating() {
        return selectedRating;
    }
    
    /**
     * Shows a preview of what the rating would look like on hover
     */
    private void previewRating(int rating) {
        for (int i = 0; i < MAX_STARS; i++) {
            if (i < rating) {
                stars[i].setText(FILLED_STAR);
                stars[i].setTextFill(Color.GOLD);
            } else {
                stars[i].setText(EMPTY_STAR);
                stars[i].setTextFill(Color.GRAY);
            }
        }
    }
    
    /**
     * Updates the display to show the current selected rating
     */
    private void updateDisplay() {
        for (int i = 0; i < MAX_STARS; i++) {
            if (i < selectedRating) {
                stars[i].setText(FILLED_STAR);
                stars[i].setTextFill(Color.GOLD);
            } else {
                stars[i].setText(EMPTY_STAR);
                stars[i].setTextFill(Color.GRAY);
            }
        }
    }
    
    /**
     * Clears the rating filter
     */
    public void clearRating() {
        setRating(0);
    }
    
    /**
     * Returns true if a minimum rating filter is active
     */
    public boolean hasRatingFilter() {
        return selectedRating > 0;
    }
}