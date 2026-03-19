package org.troy.capstone.ui_components.items.searched;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.entities.Item;
import org.troy.capstone.ui_components.items.AttributedItemContainer;

import java.text.SimpleDateFormat;
import java.lang.reflect.Field;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SearchedItemPanelTest {

    @BeforeAll
    public static void setup() {
        new JFXPanel();
    }
    
    @Test
    @DisplayName("Test SearchedItemPanel creation and content population")
    public void testSearchedItemPanelCreation() {
        Item dummyItem = Item.randomItem();
        
        SearchedItemPanel panel = new SearchedItemPanel(dummyItem);
        
        //Verify that the panel was created successfully
        assertNotNull(panel, "SearchedItemPanel should be created successfully");
        
        // Verify that the right panel is populated with the correct content
        VBox rightPanel = panel.getRightPanel();
        assertNotNull(rightPanel, "Right panel should be initialized");
        
        //Check that the right panel contains the expected number of children (name, publisher, category, price, rating, stock, date)
        assertEquals(7, rightPanel.getChildren().size(), "Right panel should contain 7 children (name, publisher, category, price, rating, stock, date)");
        
        //Verify that the name label is correct
        Label nameLabel = (Label) rightPanel.getChildren().get(0);
        assertEquals(dummyItem.getName(), nameLabel.getText(), "Name label should display the item's name");
        

        //Verify that the publisher label is correct
        Label publisherLabel = (Label) rightPanel.getChildren().get(1);
        String expectedPublisherText = "Publisher: " + dummyItem.getPublisher();
        assertEquals(expectedPublisherText, publisherLabel.getText(), "Publisher label should display the publisher's name");

        //Verify that the category label is correct
        Label categoryLabel = (Label) rightPanel.getChildren().get(2);
        String expectedCategoryText = "Category: " + dummyItem.getCategory();
        assertEquals(expectedCategoryText, categoryLabel.getText(), "Category label should display the item's category");

        //Verify that the price label is correct
        Label priceLabel = (Label) rightPanel.getChildren().get(3);
        String expectedPriceText = "Price: $" + String.format("%.2f", dummyItem.getPrice());
        assertEquals(expectedPriceText, priceLabel.getText(), "Price label should display the item's price");

        //Verify that the rating label is correct
        Label ratingLabel = (Label) rightPanel.getChildren().get(4);
        String expectedRatingText = "Rating: " + dummyItem.getReviewScore() + "/5.0 (" + dummyItem.getReviewCount() + " reviews)";
        assertEquals(expectedRatingText, ratingLabel.getText(), "Rating label should display the item's rating and review count");

        //Verify that the stock label is correct
        Label stockLabel = (Label) rightPanel.getChildren().get(5);
        String expectedStockText = "Stock: " + dummyItem.getStockQuantity();
        assertEquals(expectedStockText, stockLabel.getText(), "Stock label should display the item's stock quantity");

        //Verify that the date label is correct
        Label dateLabel = (Label) rightPanel.getChildren().get(6);
        SimpleDateFormat dateAddedFormatter;
        try{
            Field dateAddedFormatterField = SearchedItemPanel.class.getDeclaredField("dateAddedFormatter");
            dateAddedFormatterField.setAccessible(true);
            Object dateAddedFormatterObj = dateAddedFormatterField.get(null);
            assert dateAddedFormatterObj instanceof SimpleDateFormat : "Expected dateAddedFormatter to be a SimpleDateFormat, but got: " + dateAddedFormatterObj.getClass();
            dateAddedFormatter = (SimpleDateFormat) dateAddedFormatterObj;  
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access dateAddedFormatter field", e);
        }
        String expectedDateText = "Date Added: " + dateAddedFormatter.format(dummyItem.getDateAdded());
        assertEquals(expectedDateText, dateLabel.getText(), "Date label should display the formatted date added");
    
        //Ensure the attributed panel has 2 children (image and attribution flow)
        AttributedItemContainer attributedPanel = panel.getAttributedImage();
        assertNotNull(attributedPanel, "Attributed image panel should be initialized");
        assertEquals(2, attributedPanel.getChildren().size(), "Attributed image panel should contain 2 children (image and attribution flow)");
    }
}
