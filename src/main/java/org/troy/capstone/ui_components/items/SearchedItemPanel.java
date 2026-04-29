package org.troy.capstone.ui_components.items;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.constants.TestFXId;
import org.troy.capstone.entities.Item;
import org.troy.capstone.interfaces.SearchedItemPanelInteractor;
import org.troy.capstone.utils.UIUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The {@code SearchedItemPanel} class represents a UI component that displays the details of a single item in the search results.
 */
public class SearchedItemPanel extends HBox{


    /** List of interactors to handle interactions with the item panel, such as adding the item to the {@code RecentlyViewedQueue} when the panel is clicked. */
    private final List<SearchedItemPanelInteractor> interactors = new ArrayList<>();

    /**
     * Date formatter for displaying the date added attribute of the item in a user-friendly format. The format used is "MMMM dd, yyyy" (e.g., "January 01, 2020").
     */
    private static final SimpleDateFormat dateAddedFormatter = new SimpleDateFormat("MMMM dd, yyyy");

    /** The container for the attributed image of the item, displaying the item's image along with any relevant attributes. */
    private final AttributedItemContainer attributedImage;  

    /** The ID of the item being displayed in this panel. Used for checking if the panel is in the recently viewed queue. */
    private final String itemId;

    /** Static variable to track if the first instance of {@code SearchedItemPanel} has been created, used to assign an ID to the name label of the first instance for clicking on the instance in tests. */
    public static boolean firstInstanceMade = false;

    /**
     * Creates a {@code SearchedItemPanel} for the given item, displaying its image and details in a structured layout.
     * The panel consists of a left side with the attributed image and a right side with textual details about the item.
     * It also includes styling such as borders and spacing to enhance the visual presentation of the item information.
     * 
     * @pre {@code item} should contain valid data for all attributes being displayed.
     * 
     * @param item The item whose details are being displayed in this panel, used to populate both the attributed image and the other details.
     */
    private SearchedItemPanel(Item item) {
        this.itemId = item.getId();

        //Set up the left side
        attributedImage = new AttributedItemContainer(item);

        //Set up the right side with the item details
        VBox rightPanel = makeRightPanel(item);
        
        //Add both sides to the HBox
        getChildren().addAll(attributedImage, rightPanel);
        setSpacing(20);
        setAlignment(Pos.TOP_LEFT);
                
        //Add padding inside the border
        setPadding(new Insets(UISizeControl.HEIGHT_PADDING.getValue(), UISizeControl.WIDTH_PADDING.getValue(), UISizeControl.HEIGHT_PADDING.getValue(), UISizeControl.WIDTH_PADDING.getValue()));
        
        //Optimize rendering performance
        setCache(true);
        setCacheHint(CacheHint.SPEED);
        setSnapToPixel(true);
    }

    /** Factory method to create a {@code SearchedItemPanel} instance.
     * @param item The item whose details are being displayed in this panel.
     * @return A new instance of {@code SearchedItemPanel}.
     */
    public static SearchedItemPanel create(Item item) {
        SearchedItemPanel panel = new SearchedItemPanel(item);
        UIUtils.setLineBorder(panel, 5, 2);
        return panel;
    }

    /** Sets a {@code SearchedItemPanelInteractor} to the panel to allow for interaction with the item panel
     * @pre {@code interactor} should be properly implemented to handle interactions with the item panel, and the {@code SearchedItemPanel} should be properly initialized to allow for setting the interactor.
     * @post The provided {@code interactor} is set to the {@code SearchedItemPanel}, allowing it to receive interaction events from the item panel. This enables functionality such as adding the item to the {@code RecentlyViewedQueue} when the panel is clicked.
     * @param interactor The {@code SearchedItemPanelInteractor} to set for handling interactions with the item panel.
     */
    public void addSearchedItemPanelInteractor(SearchedItemPanelInteractor interactor) {
        interactors.add(interactor);
        setOnMouseClicked(e -> interactors.forEach(i -> i.onItemSelected(itemId)));
    }

    /** Getter for the item ID of the item being displayed in this panel, used for checking if the panel is in the {@code RecentlyViewedQueue}. 
     * @return The ID of the item being displayed in this panel.
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Stops the asynchronous loading of the image in the {@code AttributedItemContainer}. This method can be called when the panel is no longer visible or needed, to free up resources and prevent unnecessary loading of images that are not being displayed.
     * 
     * @post The asynchronous image loading task for the {@code AttributedItemContainer} is stopped, preventing any further loading of the image that is not being displayed.
     */
    public void stopLoadingImage() {
        attributedImage.stopLoadingImage();
    }

    /**
     * Fills the right panel with some of the data from the item.
     * 
     * @pre item should contain valid data for all the attributes being displayed.
     *      {@code rightPanel} should be properly initialized to add the labels to.
     * 
     * @post {@code rightPanel} will contain labels displaying the name, publisher, category, price, rating, stock quantity, and date added for the item, with consistent styling and formatting.
     * @param item The {@code Item} whose data is being displayed in the right panel.
     * @return A {@code VBox} containing the labels with the item details, styled and formatted for display in the right panel of the {@code SearchedItemPanel}.
     */
    public static VBox makeRightPanel(Item item) {
        VBox rightPanel = new VBox(5);
        rightPanel.setAlignment(Pos.CENTER);
        //Name label done separately so we can style it
        Label nameLabel = new Label(item.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue());
        nameLabel.setAlignment(Pos.CENTER_LEFT);
        if(!firstInstanceMade) {
            nameLabel.setId(TestFXId.FIRST_SEARCHED_ITEM_NAME_LABEL.getId());
            System.out.println("Setting ID for first searched item name label: " + nameLabel.getId());
            firstInstanceMade = true;
        }


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

        rightPanel.setId("rightPanel"+item.getId()); //Set an ID for testing purposes
        return rightPanel;
    }

    /**
     * Helper method to create a label with consistent styling for the item details in the right panel.
     * This method sets properties such as wrapping, maximum width, and alignment to ensure that the labels are displayed consistently.
     * 
     * @param text The text to display in the label.
     * @return A label with the specified text and consistent styling.
     */
    private static Label createLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue());
        label.setAlignment(Pos.CENTER_LEFT);
        return label;
    }

}
