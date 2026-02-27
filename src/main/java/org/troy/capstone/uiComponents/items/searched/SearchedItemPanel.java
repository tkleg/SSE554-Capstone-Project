package org.troy.capstone.uiComponents.items.searched;

import org.troy.capstone.entities.Item;
import org.troy.capstone.uiComponents.items.AttributedItemContainer;

import javafx.geometry.Insets;
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

    private final AttributedItemContainer attributedImage;
    private final VBox rightPanel;

    public SearchedItemPanel(Item item) {

        //Set up the left side
        attributedImage = new AttributedItemContainer(item);

        //Set up the right side - text content
        rightPanel = new VBox(5); // 5px spacing between elements
        fillRightPanel(item);
        
        //Add both sides to the HBox
        getChildren().addAll(attributedImage, rightPanel);
        setSpacing(20); // 20px spacing between image and text
        
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
        Label nameLabel = new Label("Name: " + item.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        rightPanel.getChildren().addAll(
            nameLabel,
            new Label("Publisher: " + item.getPublisher()),
            new Label("Category: " + item.getCategory()),
            new Label("Price: $" + String.format("%.2f", item.getPrice())),
            new Label("Rating: " + item.getReviewScore() + "/5.0 (" + item.getReviewCount() + " reviews)"),
            new Label("Stock: " + item.getStockQuantity()),
            new Label("Date Added: " + item.getDateAdded().toString())
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
