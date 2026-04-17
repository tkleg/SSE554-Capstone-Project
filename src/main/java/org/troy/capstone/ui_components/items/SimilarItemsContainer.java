package org.troy.capstone.ui_components.items;

import java.util.List;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.interfaces.SearchedItemPanelDestinationUI;
import org.troy.capstone.utils.UIUtils;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Container to display similar items. Uses an HBox layout and houses SearchedItemPanels */
public class SimilarItemsContainer extends HBox implements SearchedItemPanelDestinationUI{

    /** Constructor for SimilarItemsContainer. Initializes the container with a title label and sets up the layout. 
     * @pre The SimilarItemsContainer should be properly initialized to contain an HBox layout with a title label.
     * @post The SimilarItemsContainer instance is created with an HBox layout containing a title label, ready to have similar items added to it.
     */
    private SimilarItemsContainer() {
        super();
        Label titleLabel = new Label("Similar Items");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        getChildren().add(titleLabel);
    }

    /** Factory method to create a SimilarItemsContainer instance with a title label and appropriate styling. 
    * @return A SimilarItemsContainer instance with a title label and appropriate styling, ready to have similar items added to it.
    */
    public static SimilarItemsContainer create() {
        SimilarItemsContainer container = new SimilarItemsContainer();
        UIUtils.setSize(container, UISizeControl.SIMILAR_ITEMS_CONTAINER_WIDTH.getValue(), UISizeControl.SIMILAR_ITEMS_CONTAINER_HEIGHT.getValue());
        UIUtils.setLineBorder(container, 5, 1);
        return container;
    }

    @Override
    public void setContent(List<Node> panels) {
        getChildren().clear();
        getChildren().addAll(panels);
    }

}
