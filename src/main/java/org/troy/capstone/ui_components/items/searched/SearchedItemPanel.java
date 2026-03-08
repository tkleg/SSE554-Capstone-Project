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

public class SearchedItemPanel extends HBox{

    private static final SimpleDateFormat dateAddedFormatter = new SimpleDateFormat("MMMM dd, yyyy");

    private final AttributedItemContainer attributedImage;
    private final VBox rightPanel;
    
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
     * pre-conditions: item should contain valid data for all the attributes being displayed,
     *  and the rightPanel should be properly initialized to add the labels to.
     * 
     * @param item ( Item ) : The item whose data is being displayed in the right panel.
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

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue());
        label.setAlignment(Pos.CENTER_LEFT);
        return label;
    }

    public VBox getRightPanel() {
        return rightPanel;
    }
    
    public AttributedItemContainer getAttributedImage() {
        return attributedImage;
    }

    private void setBorder(){
        setBorder(new Border(new BorderStroke(
            Color.BLACK, 
            BorderStrokeStyle.SOLID, 
            new CornerRadii(5), 
            new BorderWidths(2)
        )));
    }

}
