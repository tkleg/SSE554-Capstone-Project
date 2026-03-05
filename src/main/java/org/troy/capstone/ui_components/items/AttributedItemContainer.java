package org.troy.capstone.ui_components.items;

import java.awt.Desktop;
import java.net.URI;

import org.troy.capstone.constants.URL;
import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.entities.Item;
import org.troy.capstone.utils.UIUtils;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AttributedItemContainer extends VBox {

    private final ImageView imageView;

    public static AttributedItemContainer createFromItem(Item item) {
        AttributedItemContainer container = new AttributedItemContainer(item);
        UIUtils.setSize(container, UISizeControl.ATTRIBUTED_ITEM_CONTAINER_WIDTH.getValue(), null);
        return container;
    }

    public AttributedItemContainer(Item item) {
        super(5); //5px spacing between items
        setAlignment(Pos.TOP_CENTER); // Center-align the image and attribution

        TextFlow attributionFlow = makeAttributionFlow(item);

        imageView = new ImageView();
        imageView.setFitWidth(UISizeControl.ATTRIBUTED_ITEM_IMAGE_WIDTH.getValue());
        imageView.setFitHeight(UISizeControl.ATTRIBUTED_ITEM_IMAGE_HEIGHT.getValue());
        imageView.setPreserveRatio(true);
        
        //Load image asynchronously to avoid blocking scroll
        loadImageAsync(item.getImageUrl());
        
        imageView.setOnMouseClicked(e -> {
            try {
                Desktop.getDesktop().browse(new URI(item.getImageUrl()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        //Optimize rendering
        setCache(true);
        setCacheHint(javafx.scene.CacheHint.SPEED);

        getChildren().addAll(imageView, attributionFlow);
    }

    /**
     * Creates a TextFlow for the attribution text with clickable links for the author and source.
     * 
     * pre-conditions: item should contain valid data for the photo author and their URL, as well as the source URL for Unsplash.
     * 
     * @param item (Item) : The item whose data is being used to create the attribution flow, specifically the photo author and their URL.
     * @return TextFlow : A TextFlow containing the attribution text with clickable links for the author and source.
     */
    TextFlow makeAttributionFlow(Item item) {
        Text text1 = new Text("Photo by ");
        Text authorName = new Text(item.getPhotoAuthor());
        authorName.setUnderline(true);
        Text text2 = new Text(" on ");
        Text sourceName = new Text("Unsplash"); 
        sourceName.setUnderline(true); 

        authorName.setOnMouseClicked(e ->{
            try {
                Desktop.getDesktop().browse(new URI(item.getPhotoAuthorUrl()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        sourceName.setOnMouseClicked(e ->{
            try {
                Desktop.getDesktop().browse( new URI( URL.UNSPLASH_ATTRIBUTION.getUrl() ) );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        //Create the TextFlow and add all the text nodes to it
        return new TextFlow(text1, authorName, text2, sourceName);
    }
    
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Loads an image from a URL asynchronously to avoid blocking the UI thread,
     *  and sets it to the imageView once loaded.
     * 
     * @param imageUrl (String) : The URL of the image to be loaded.
     */
    private void loadImageAsync(String imageUrl) {
        Task<Image> imageTask = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                return new Image(imageUrl, true);
            }
        };
        
        imageTask.setOnSucceeded(e -> {
            Image image = imageTask.getValue();
            if (image != null)
                imageView.setImage(image);
        });
        
        imageTask.setOnFailed(e -> {
            System.err.println("Failed to load image: " + imageUrl);
        });
        
        Thread imageThread = new Thread(imageTask);
        imageThread.setDaemon(true);//Allow JVM to exit if these threads are the only ones left
        imageThread.start();
    }
}
