package org.troy.capstone.ui_components.items.searched;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.entities.Item;
import org.troy.capstone.ui_components.items.AttributedItemContainer;
import org.troy.capstone.utils.UIUtils;

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

    public static SearchedItemPanel createFromItem(Item item) {
        SearchedItemPanel panel =  new SearchedItemPanel(item);
        UIUtils.setSize(panel, UISizeControl.SEARCHED_ITEM_PANEL_WIDTH.getValue(), UISizeControl.SEARCHED_ITEM_PANEL_HEIGHT.getValue());
        return panel;
    }
    
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

    private void fillRightPanel(Item item) {
        //Name label done separately so we can style it
        Label nameLabel = new Label(item.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue()); // Allow space for image on left
        nameLabel.setAlignment(Pos.CENTER_LEFT);

        // Create labels with text wrapping enabled
        Label publisherLabel = new Label("Publisher: " + item.getPublisher());
        publisherLabel.setWrapText(true);
        publisherLabel.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue());
        publisherLabel.setAlignment(Pos.CENTER_LEFT);
        
        Label categoryLabel = new Label("Category: " + item.getCategory());
        categoryLabel.setWrapText(true);
        categoryLabel.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue());
        categoryLabel.setAlignment(Pos.CENTER_LEFT);
        
        Label priceLabel = new Label("Price: $" + String.format("%.2f", item.getPrice()));
        priceLabel.setWrapText(true);
        priceLabel.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue());
        priceLabel.setAlignment(Pos.CENTER_LEFT);
        
        Label ratingLabel = new Label("Rating: " + item.getReviewScore() + "/5.0 (" + item.getReviewCount() + " reviews)");
        ratingLabel.setWrapText(true);
        ratingLabel.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue());
        ratingLabel.setAlignment(Pos.CENTER_LEFT);
        
        Label stockLabel = new Label("Stock: " + item.getStockQuantity());
        stockLabel.setWrapText(true);
        stockLabel.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue());
        stockLabel.setAlignment(Pos.CENTER_LEFT);
        
        Date dateAdded = item.getDateAdded();
        Label dateLabel = new Label("Date Added: " + dateAddedFormatter.format(dateAdded));
        dateLabel.setWrapText(true);
        dateLabel.setMaxWidth(UISizeControl.SEARCHED_ITEM_LABEL_MAX_WIDTH.getValue());
        dateLabel.setAlignment(Pos.CENTER_LEFT);

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
    
    private void setBorder(){
        setBorder(new Border(new BorderStroke(
            Color.BLACK, 
            BorderStrokeStyle.SOLID, 
            new CornerRadii(5), 
            new BorderWidths(2)
        )));
    }

}
