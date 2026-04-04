package org.troy.capstone.ui_components.items;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.entities.Item;
import org.troy.capstone.interfaces.SearchedItemPanelInteractor;
import org.troy.capstone.utils.UIUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
    

    /** The ID of the item being displayed in this panel. Used for checking if the panel is in the recently viewed queue. */
    private final String itemId;

    /**
     * Creates a SearchedItemPanel for the given item, displaying its image and details in a structured layout.
     * The panel consists of a left side with the attributed image and a right side with textual details about the item.
     * It also includes styling such as borders and spacing to enhance the visual presentation of the item information.
     * 
     * @pre item should contain valid data for all attributes being displayed.
     *      The AttributedItemContainer should be properly initialized to display the item's image and attributes.
     *      The rightPanel should be properly initialized to display the item's textual details.
     * 
     * @param item The item whose details are being displayed in this panel, used to populate both the attributed image and the other details.
     */
    private SearchedItemPanel(Item item) {
        this.itemId = item.getId();

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
                
        //Add padding inside the border
        setPadding(new Insets(UISizeControl.HEIGHT_PADDING.getValue(), UISizeControl.WIDTH_PADDING.getValue(), UISizeControl.HEIGHT_PADDING.getValue(), UISizeControl.WIDTH_PADDING.getValue()));
        
        //Optimize rendering performance
        setCache(true);
        setCacheHint(javafx.scene.CacheHint.SPEED);
        setSnapToPixel(true);
    }

    /** Factory method to create a SearchedItemPanel instance.
     * @param item The item whose details are being displayed in this panel.
     * @return A new instance of SearchedItemPanel.
     */
    public static SearchedItemPanel create(Item item) {
        SearchedItemPanel panel = new SearchedItemPanel(item);
        UIUtils.setLineBorder(panel, 5, 2);
        return panel;
    }

    /** Sets a SearchedItemPanelInteractor to the panel to allow for interaction with the item panel
     * @pre interactor should be properly implemented to handle interactions with the item panel, and the SearchedItemPanel should be properly initialized to allow for setting the interactor.
     * @post The provided interactor is set to the SearchedItemPanel, allowing it to receive interaction events from the item panel. This enables functionality such as adding the item to the recently viewed queue when the panel is clicked.
     */
    public void setSearchedItemPanelInteractor(SearchedItemPanelInteractor interactor) {
        setOnMouseClicked(e -> interactor.onItemSelected(itemId));
    }

    /** Getter for the item ID of the item being displayed in this panel, used for checking if the panel is in the recently viewed queue. 
     * @return The ID of the item being displayed in this panel.
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Stops the asynchronous loading of the image in the attributed image container. This method can be called when the panel is no longer visible or needed, to free up resources and prevent unnecessary loading of images that are not being displayed.
     * 
     * @post The asynchronous image loading task for the attributed image container is stopped, preventing any further loading of the image that is not being displayed.
     */
    public void stopLoadingImage() {
        attributedImage.stopLoadingImage();
    }

    /**
     * Fills the right panel with some of the data from the item.
     * 
     * @pre item should contain valid data for all the attributes being displayed.
     *      rightPanel should be properly initialized to add the labels to.
     * 
     * @post rightPanel will contain labels displaying the name, publisher, category, price, rating, stock quantity, and date added for the item, with consistent styling and formatting.
     * @param item The item whose data is being displayed in the right panel.
     */
    private void fillRightPanel(Item item) {
        //Name label done separately so we can style it
        Label nameLabel = new Label(item.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue()); // Allow space for image on left
        nameLabel.setAlignment(Pos.CENTER_LEFT);
        //nameLabel.setOnMouseClicked(e -> { recentlyViewedManager.addRecentlyViewedItem(item.getId());});

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

}
