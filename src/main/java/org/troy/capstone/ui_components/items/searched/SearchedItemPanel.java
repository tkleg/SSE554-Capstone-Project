package org.troy.capstone.ui_components.items.searched;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.entities.Item;
import org.troy.capstone.ui_components.items.AttributedItemContainer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * The SearchedItemPanel class represents a UI component that displays the details of a single item in the search results.
 */
public class SearchedItemPanel extends HBox{

    /**
     * Date formatter for displaying the date added attribute of the item in a user-friendly format. The format used is "MMMM dd, yyyy" (e.g., "January 01, 2020").
     */
    private static final SimpleDateFormat dateAddedFormatter = new SimpleDateFormat("MMMM dd, yyyy");

    /** The container for the attributed image of the item, displaying the item's image along with any relevant attributes. */
    private final AttributedItemContainer attributedImage;
    /** The container for the textual details of the item, displayed on the right side of the panel. */
    private final VBox rightPanel;
    
    /**
     * Creates a SearchedItemPanel for the given item, displaying its image and details in a structured layout.
     * The panel consists of a left side with the attributed image and a right side with textual details about the item.
     * It also includes styling such as borders and spacing to enhance the visual presentation of the item information.
     * 
     * @pre <ul><li>item should contain valid data for all attributes being displayed.</li>
     *      <li>The AttributedItemContainer should be properly initialized to display the item's image and attributes.</li>
     *      <li>The rightPanel should be properly initialized to display the item's textual details.</li></ul>
     * 
     * @param item The item whose details are being displayed in this panel, used to populate both the attributed image and the other details.
     */
    public SearchedItemPanel(Item item) {

        //Set up the left side
        attributedImage = new AttributedItemContainer(item);

        //Set up the right side - text content
        rightPanel = new VBox(5); // 5px spacing between elements
        rightPanel.setAlignment(Pos.TOP_LEFT); // Align content to top-left
        fillRightPanel(item);
        
        //Add both sides to the HBox
        getChildren().addAll(attributedImage, rightPanel);
        setSpacing(20); // 20px spacing between image and text
        setAlignment(Pos.TOP_LEFT); // Align all items to top-left for consistency
        
        //Add border to the panel
        setBorder();
        
        //Add padding inside the border
        setPadding(new Insets(10));
        
        //Optimize rendering performance
        setCache(true);
        setCacheHint(javafx.scene.CacheHint.SPEED);
        setSnapToPixel(true);
    }

    /**
     * Fills the right panel with some of the data from the item.
     * 
     * @pre <ul><li>item should contain valid data for all the attributes being displayed.</li>
     *      <li>rightPanel should be properly initialized to add the labels to.</li></ul>
     * 
     * @param item The item whose data is being displayed in the right panel.
     */
    private void fillRightPanel(Item item) {
        //Name label done separately so we can style it
        Label nameLabel = new Label(item.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue()); // Allow space for image on left
        nameLabel.setAlignment(Pos.CENTER_LEFT);

        Label publisherLabel = createLabel("Publisher: " + item.getPublisher());
        
        Label categoryLabel = createLabel("Category: " + item.getCategory());
        
        Label priceLabel = createLabel("Price: $" + String.format("%.2f", item.getPrice()));
        
        Label ratingLabel = createLabel("Rating: " + item.getReviewScore() + "/5.0 (" + item.getReviewCount() + " reviews)");
        
        Label stockLabel = createLabel("Stock: " + item.getStockQuantity());
        
        Date dateAdded = item.getDateAdded();
        Label dateLabel = createLabel("Date Added: " + dateAddedFormatter.format(dateAdded));

        rightPanel.getChildren().addAll(
            nameLabel,
            publisherLabel,
            categoryLabel,
            priceLabel,
            ratingLabel,
            stockLabel,
            dateLabel
        );
    }

    /**
     * Helper method to create a label with consistent styling for the item details in the right panel.
     * This method sets properties such as wrapping, maximum width, and alignment to ensure that the labels are displayed consistently.
     * 
     * @param text The text to display in the label.
     * @return A label with the specified text and consistent styling.
     */
    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue());
        label.setAlignment(Pos.CENTER_LEFT);
        return label;
    }

    /**
     * Getter for the right panel of the SearchedItemPanel, which contains the textual details of the item.
     * @return The VBox containing the textual details of the item, displayed on the right side of the panel.
     */
    public VBox getRightPanel() {
        return rightPanel;
    }
    
    /**
     * Getter for the attributed image container of the SearchedItemPanel, which displays the item's image and relevant attributes.
     * @return The AttributedItemContainer containing the item's image and relevant attributes, displayed on the left side of the panel.
     */
    public AttributedItemContainer getAttributedImage() {
        return attributedImage;
    }

    /**
     * Sets the border for the SearchedItemPanel with a consistent style.
     * This method creates a black solid border with rounded corners and a specified width.
     */
    private void setBorder(){
        setBorder(new Border(new BorderStroke(
            Color.BLACK, 
            BorderStrokeStyle.SOLID, 
            new CornerRadii(5), 
            new BorderWidths(2)
        )));
    }

}
