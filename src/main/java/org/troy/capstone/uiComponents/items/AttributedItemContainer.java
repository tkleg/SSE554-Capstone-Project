package org.troy.capstone.uiComponents.items;

import java.awt.Desktop;
import java.net.URI;

import org.troy.capstone.constants.URLs;
import org.troy.capstone.entities.Item;

import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AttributedItemContainer extends VBox {


    private final ImageView imageView;

    public AttributedItemContainer(Item item) {
        super(5); // 5px spacing between items

        TextFlow attributionFlow = makeAttributionFlow(item);

        imageView = new ImageView( item.getImageUrl() );
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.setOnMouseClicked(e -> {
            try {
                Desktop.getDesktop().browse(new URI(item.getImageUrl()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        getChildren().addAll(imageView, attributionFlow);
    }

    private TextFlow makeAttributionFlow(Item item) {
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
                Desktop.getDesktop().browse( new URI( URLs.UNSPLASH_ATTRIBUTION ) );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        // Create the TextFlow and add all the text nodes to it
        return new TextFlow(text1, authorName, text2, sourceName);
    }
}
