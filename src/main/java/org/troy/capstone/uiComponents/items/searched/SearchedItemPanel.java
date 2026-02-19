package org.troy.capstone.uiComponents.items.searched;

import java.awt.Desktop;
import java.io.IOException;

import org.troy.capstone.entities.Item;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SearchedItemPanel extends HBox{

    private final ImageView imageView;
    private final VBox rightPanel;

    public SearchedItemPanel(Item item) {
        // Set up the left side - image
        imageView = new ImageView( item.getImageUrl() );
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        imageView.setOnMouseClicked(e -> {
            //URL will be replaced with item.getImageUrl() once the real image URLs are added to the dataset
            String url = "https://unsplash.com/@kalenemsley";
            try {
                Desktop.getDesktop().browse(java.net.URI.create(url));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });
        
        //Name label done separately so we can style it later
        Label nameLabel = new Label("Name: " + item.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        // Set up the right side - text content
        rightPanel = new VBox(5); // 5px spacing between elements
        rightPanel.getChildren().addAll(
            nameLabel,
            new Label("Publisher: " + item.getPublisher()),
            new Label("Category: " + item.getCategory()),
            new Label("Price: $" + String.format("%.2f", item.getPrice())),
            new Label("Rating: " + item.getReviewScore() + "/5.0 (" + item.getReviewCount() + " reviews)"),
            new Label("Stock: " + item.getStockQuantity()),
            new Label("Date Added: " + item.getDateAdded().toString())
        );
        
        // Add both sides to the HBox
        getChildren().addAll(imageView, rightPanel);
        setSpacing(20); // 20px spacing between image and text
        
        // Add border to the panel
        setBorder(new Border(new BorderStroke(
            Color.BLACK, 
            BorderStrokeStyle.SOLID, 
            new CornerRadii(5), 
            new BorderWidths(2)
        )));
        
        // Add padding inside the border
        setPadding(new Insets(10));
    }
}
