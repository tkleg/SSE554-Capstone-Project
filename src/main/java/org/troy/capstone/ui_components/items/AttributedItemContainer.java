package org.troy.capstone.ui_components.items;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.troy.capstone.annotations.Generated;
import org.troy.capstone.constants.TestFXId;
import org.troy.capstone.constants.URL;
import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.entities.Item;

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

    /** The task used to load the image asynchronously. */
    private Task<Image> loadImageTask;


    /** The Desktop instance used to open URLs in the default browser. Used in testing to allow for mocking to disable actual browser opening. */
    @SuppressWarnings("FieldMayBeFinal")
    private static Desktop desktop = Desktop.getDesktop();

    /** Creates an AttributedItemContainer for the given item, initializing the image view and attribution flow.
     * 
     * @pre item should contain valid data for the image URL and attribution information.
     *      The AttributedItemContainer should be properly initialized to display the item's image and attribution information.
     *      The loadImageTask should be not initialized, it will be set up to load the image asynchronously within this constructor.
     * @post The variable passed into loadImageTask will be initialized to a Task that loads the image from the item's image URL asynchronously.
     * 
     * @param item The item whose image and attribution information are being displayed in this container.
     */
    public AttributedItemContainer(Item item) {
        super(5); //5px spacing between items
        setAlignment(Pos.TOP_CENTER); // Center-align the image and attribution

        TextFlow attributionFlow = makeAttributionFlow(item);

        imageView = new ImageView();
        imageView.setId(TestFXId.ATTRIBUTED_IMAGE_VIEW_PREFIX.getId() + item.getId());
        imageView.setFitWidth(UISizeControl.ATTRIBUTED_ITEM_IMAGE_WIDTH.getValue());
        imageView.setFitHeight(UISizeControl.ATTRIBUTED_ITEM_IMAGE_HEIGHT.getValue());
        imageView.setPreserveRatio(true);
        
        //Load image asynchronously to avoid blocking scroll
        loadImageTask = loadImageAsync(item.getImageUrl());
        
        imageView.setOnMouseClicked(e -> {
            try {
                desktop.browse(new URI(item.getImageUrl()));
            } catch (IOException | URISyntaxException ex) {
                System.err.println("Failed to open image URL: " + item.getImageUrl());
            }
        });
        
        //Optimize rendering
        setCache(true);
        setCacheHint(javafx.scene.CacheHint.SPEED);

        getChildren().addAll(imageView, attributionFlow);
    }

    /** Stops the asynchronous loading of the image in this AttributedItemContainer. This method can be called when the container is no longer visible or needed, to free up resources and prevent unnecessary loading of images that are not being displayed. 
     * @post The asynchronous image loading task for this AttributedItemContainer is stopped, preventing any further loading of the image that is not being displayed.
    */
    @Generated
    public void stopLoadingImage() {
        if (loadImageTask != null && loadImageTask.isRunning())
            loadImageTask.cancel();
    }

    /**
     * Creates a TextFlow for the attribution text with clickable links for the author and source.
     * 
     * @pre item should contain valid data for the photo author and their URL, as well as the source URL for Unsplash.
     * 
     * @param item The item whose data is being used to create the attribution flow, specifically the photo author and their URL.
     * @return A TextFlow containing the attribution text with clickable links for the author and source.
     */
    @SuppressWarnings("FinalPrivateMethod")
    private final TextFlow makeAttributionFlow(Item item) {
        Text text1 = new Text("Photo by ");
        Text authorName = new Text(item.getPhotoAuthor());
        authorName.setUnderline(true);
        authorName.setId(TestFXId.ATTRIBUTED_AUTHOR_NAME_PREFIX.getId() + item.getId());
        Text text2 = new Text(" on ");
        Text sourceName = new Text("Unsplash"); 
        sourceName.setUnderline(true); 
        sourceName.setId(TestFXId.ATTRIBUTED_SOURCE_NAME_PREFIX.getId() + item.getId());

        authorName.setOnMouseClicked(e ->{
            try {
                desktop.browse(new URI(item.getPhotoAuthorUrl()));
            } catch (IOException | URISyntaxException ex) {
                System.err.println("Failed to open author URL: " + item.getPhotoAuthorUrl());
            }
        });
        sourceName.setOnMouseClicked(e ->{
            try {
                desktop.browse( new URI( URL.UNSPLASH_ATTRIBUTION.getUrl() ) );
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
     * @post The image from the specified URL will be loaded and displayed in the imageView of the AttributedItemContainer.
     * @param imageUrl The URL of the image to be loaded.
     * @return A Task that loads the image from the specified URL and updates the imageView upon completion.
     */
    private Task<Image> loadImageAsync(String imageUrl) {
        loadImageTask = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                return new Image(imageUrl, true);
            }
        };
        
        loadImageTask.setOnSucceeded(e -> {
            imageView.setImage(loadImageTask.getValue());
        });
        
        Thread imageThread = new Thread(loadImageTask);
        imageThread.setDaemon(true);//Allow JVM to exit if these threads are the only ones left
        imageThread.start();
        return loadImageTask;
    }
}
