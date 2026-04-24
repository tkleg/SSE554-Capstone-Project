package org.troy.capstone.ui_components.items;

import java.util.List;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.interfaces.SearchedItemPanelDestinationUI;
import org.troy.capstone.utils.UIUtils;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;

/** Container to display similar items. Uses an {@code HBox} layout and houses {@code SearchedItemPanels} */
public class SimilarItemsContainer extends ScrollPane implements SearchedItemPanelDestinationUI{

    /** The {@code HBox} that holds the content of the container. */
    private final HBox content;

    /** Constructor for {@code SimilarItemsContainer}. Initializes the container with a title label and sets up the layout. 
     * @pre The {@code SimilarItemsContainer} should be properly initialized to contain a {@code ScrollPane} with an {@code HBox} layout and a title label.
     * @post The {@code SimilarItemsContainer} instance is created with a {@code ScrollPane} containing an {@code HBox} layout and a title label, ready to have similar items added to it.
     */
    private SimilarItemsContainer() {
        super();
        content = new HBox();
        Label titleLabel = new Label("Similar Items");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        content.getChildren().add(titleLabel);
        setContent(content);
        setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        setVbarPolicy(ScrollBarPolicy.NEVER);
        setFitToWidth(false);
    }

    /** Factory method to create a {@code SimilarItemsContainer} instance with a title label and appropriate styling. 
    * @return A {@code SimilarItemsContainer} instance with a title label and appropriate styling, ready to have similar items added to it.
    */
    public static SimilarItemsContainer create() {
        SimilarItemsContainer container = new SimilarItemsContainer();
        UIUtils.setSize(container, UISizeControl.SIMILAR_ITEMS_CONTAINER_WIDTH.getValue(), UISizeControl.SIMILAR_ITEMS_CONTAINER_HEIGHT.getValue());
        UIUtils.setLineBorder(container, 5, 1);
        Label titleLabel = new Label("Similar Items");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        container.content.getChildren().add(titleLabel);
        return container;
    }

    /** Sets the content of the {@code SimilarItemsContainer} with the given list of {@code Node} instances representing similar items.
     * @pre The panels list should contain valid {@code Node} instances to be displayed in the {@code SimilarItemsContainer}.
     * @post The {@code SimilarItemsContainer}'s content is updated to display the provided {@code Node} instances representing similar items.
     * @param panels The list of {@code Node} instances to be displayed in the {@code SimilarItemsContainer}.
     */
    @Override
    public void setContent(List<Node> panels) {
        content.getChildren().clear();
        content.getChildren().addAll(panels);
    }

}
