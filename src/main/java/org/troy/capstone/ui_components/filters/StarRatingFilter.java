package org.troy.capstone.ui_components.filters;

import org.troy.capstone.constants.TestFXId;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * The {@code StarRatingFilter} class represents a UI component that allows users to select a minimum star rating for filtering search results. It displays a row of stars that can be clicked to set the desired rating, and it updates the display to show the selected rating.
 */
public class StarRatingFilter extends VBox {
    /** The maximum number of stars in the filter. */
    private static final int MAX_STARS = 5;
    /** Unicode character for a filled star. */
    private static final String FILLED_STAR = "★";
    /** Unicode character for an empty star. */
    private static final String EMPTY_STAR = "☆";
    
    /** The container for the star labels. */
    private final HBox starContainer = new HBox();

    /** The label that displays the current minimum rating selected by the user. */
    private final Label ratingLabel = new Label("Minimum Rating: 0 stars");

    /** An array of Labels representing the star icons in the filter. */
    final Label[] stars;
    /** The currently selected minimum rating. */
    private int selectedRating = 0;

    /**
     * Constructor for StarRatingFilter with 5 clickable stars and a label. The filter starts with no rating selected (0 stars).
     */
    public StarRatingFilter() {
        stars = new Label[MAX_STARS];
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(2);
        setId(TestFXId.STAR_RATING_FILTER.getId());
        //Create the star labels
        for (int i = 0; i < MAX_STARS; i++) {
            Label star = new Label(EMPTY_STAR);
            star.setId(TestFXId.STAR_LABEL_PREFIX.getId() + (i + 1)); //ID is starLabel_1, starLabel_2, etc.
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
            starContainer.getChildren().add(star);
        }
        
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);

        getChildren().addAll(ratingLabel, starContainer);
        updateDisplay();
    }
    
    /**
     * Sets the minimum rating (1-5).
     * 
     * @post The star labels are updated to reflect the selected rating, and the rating label is updated to show the current minimum rating. If the selected rating is clicked again, it resets to 0 (no filter).
     * 
     * @param rating The rating to set to, resets to 0 if the selected rating is clicked again.
     */
    public void setRating(int rating) {
        //If clicking the same star that's already selected, clear the rating
        if (selectedRating == rating)
            selectedRating = 0;
        else
            selectedRating = rating;
        
        updateDisplay();

        String starText = selectedRating == 1 ? "star" : "stars";
        ratingLabel.setText("Minimum Rating: " + selectedRating + " " + starText);
    }
    
    /**
     * Gets the current minimum rating (0-5, where 0 means no filter).
     * @return The current minimum rating selected in the filter.
     */
    public int getRating() {
        return selectedRating;
    }
    
    /**
     * Shows a preview of what the rating would look like on hover.
     * 
     * @post The star labels are updated to reflect the hovered rating, but the selectedRating is not changed until a click occurs. When the hover ends, the display reverts to showing the currently selected rating.
     * 
     * @param rating The rating to preview (1-5)
     */
    void previewRating(int rating) {
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
     * Updates the display to show the current selected rating.
     * Stars to the left of the selected rating are filled, and stars to the right are empty.
     * 
     * @post The star labels are updated to reflect the currently selected rating.
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
     * Gets the currently selected minimum star rating.
     * @return The currently selected minimum star rating.
     */
    public int getSelectedRating() {
        return selectedRating;
    }

}