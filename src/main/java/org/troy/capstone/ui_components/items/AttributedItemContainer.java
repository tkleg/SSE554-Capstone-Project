package org.troy.capstone.ui_components.items;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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

/**
 * The AttributedItemContainer class represents a UI component that displays an item's image along with its attribution information.
 */
public class AttributedItemContainer extends VBox {

    /** The ImageView for displaying the item's image. */
    private final ImageView imageView;

    /** Creates an AttributedItemContainer from the given item, setting its size based on UISizeControl constants.
     * 
     * @pre <ul><li>item should contain valid data for the image URL and attribution information.</li>
     *      <li>The AttributedItemContainer should be properly initialized to display the item's image and attribution information.</li></ul>
     * 
     * @param item The item whose image and attribution information are being displayed in this container, used to populate the image and attribution flow.
     * @return An AttributedItemContainer instance with the item's image and attribution information displayed, and sized according to UISizeControl constants. 
    */
    public static AttributedItemContainer createFromItem(Item item) {
        AttributedItemContainer container = new AttributedItemContainer(item);
        UIUtils.setSize(container, UISizeControl.ATTRIBUTED_ITEM_CONTAINER_WIDTH.getValue(), null);
        return container;
    }

    /** Creates an AttributedItemContainer for the given item, initializing the image view and attribution flow.
     * 
     * @pre <ul><li>item should contain valid data for the image URL and attribution information.</li>
     *      <li>The AttributedItemContainer should be properly initialized to display the item's image and attribution information.</li></ul>
     * 
     * @param item The item whose image and attribution information are being displayed in this container.
     */
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
            } catch (IOException | URISyntaxException ex) {
                System.err.println("Failed to open image URL: " + item.getImageUrl());
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
     * @pre <ul><li>item should contain valid data for the photo author and their URL, as well as the source URL for Unsplash.</li></ul>
     * 
     * @param item The item whose data is being used to create the attribution flow, specifically the photo author and their URL.
     * @return A TextFlow containing the attribution text with clickable links for the author and source.
     */
    @SuppressWarnings("FinalPrivateMethod")
    private final TextFlow makeAttributionFlow(Item item) {
        Text text1 = new Text("Photo by ");
        Text authorName = new Text(item.getPhotoAuthor());
        authorName.setUnderline(true);
        Text text2 = new Text(" on ");
        Text sourceName = new Text("Unsplash"); 
        sourceName.setUnderline(true); 

        authorName.setOnMouseClicked(e ->{
            try {
                Desktop.getDesktop().browse(new URI(item.getPhotoAuthorUrl()));
            } catch (IOException | URISyntaxException ex) {
                System.err.println("Failed to open author URL: " + item.getPhotoAuthorUrl());
            }
        });
        sourceName.setOnMouseClicked(e ->{
            try {
                Desktop.getDesktop().browse( new URI( URL.UNSPLASH_ATTRIBUTION.getUrl() ) );
            } catch (IOException | URISyntaxException ex) {
                System.err.println("Failed to open source URL: " + URL.UNSPLASH_ATTRIBUTION.getUrl());
            }
        });
        
        //Create the TextFlow and add all the text nodes to it
        return new TextFlow(text1, authorName, text2, sourceName);
    }
    
    /**
     * Getter for the ImageView in the AttributedItemContainer, which displays the item's image.
     * @return The ImageView displaying the item's image in the AttributedItemContainer.
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Loads an image from a URL asynchronously to avoid blocking the UI thread,
     *  and sets it to the imageView once loaded.
     * 
     * @param imageUrl The URL of the image to be loaded.
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
