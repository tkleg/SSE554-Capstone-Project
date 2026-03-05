package org.troy.capstone.ui_components.filters.stars;

import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.managers.GeneralManager;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class StarRatingFilter extends VBox {
    static final int MAX_STARS = 5;
    static final String FILLED_STAR = "★";
    static final String EMPTY_STAR = "☆";
    
    //Holds the stars
    private final HBox starContainer = new HBox();

    //Says "Minimum Rating" above the stars
    private final Label ratingLabel = new Label("Minimum Rating: 0 stars");

    final Label[] stars;
    private int selectedRating = 0;

    /**
     * Creates a StarRatingFilter and registers it with the GeneralManager.
      * @param generalManager (GeneralManager) : The GeneralManager to register the filter with.
      * @return filter (StarRatingFilter) : The created StarRatingFilter instance.
     */
    public static StarRatingFilter create(GeneralManager generalManager) {
        StarRatingFilter filter = new StarRatingFilter();
        generalManager.addUIElement(UIElementName.STAR_RATING_FILTER, filter);
        filter.setAlignment(Pos.CENTER_LEFT);
        filter.setSpacing(5);
        return filter;
    }

    /**
     * Initializes a StarRatingFilter with 5 clickable stars and a label. The filter starts with no rating selected (0 stars).
     * @return StarRatingFilter : The created StarRatingFilter instance.
     */
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
            starContainer.getChildren().add(star);
        }
        
        getChildren().addAll(ratingLabel, starContainer);
        updateDisplay();
    }
    
    /**
     * Sets the minimum rating (1-5). Use 0 to clear the filter.
     * 
     * @param rating (int) : The rating to set to, resets to 0 if the selected rating is clicked again.
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
     * Gets the current minimum rating (0-5, where 0 means no filter)
     * @return selectedRating (int) : The current minimum rating selected in the filter.
     */
    public int getRating() {
        return selectedRating;
    }
    
    /**
     * Shows a preview of what the rating would look like on hover.
     * @param rating (int) : The rating to preview (1-5)
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

    public int getSelectedRating() {
        return selectedRating;
    }

}