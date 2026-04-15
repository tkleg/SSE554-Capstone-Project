package org.troy.capstone.ui_components.items;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.utils.UIUtils;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class SimilarItemsContainer extends HBox {

    public SimilarItemsContainer() {
        super();
        Label titleLabel = new Label("Similar Items");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        getChildren().add(titleLabel);
    }

    public static SimilarItemsContainer create() {
        SimilarItemsContainer container = new SimilarItemsContainer();
        UIUtils.setSize(container, UISizeControl.RECENTLY_VIEWED_WINDOW_HEIGHT.getValue(), UISizeControl.RECENTLY_VIEWED_WINDOW_WIDTH.getValue());
        UIUtils.setLineBorder(container, 5, 1);
        return container;
    }
}
