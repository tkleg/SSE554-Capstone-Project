package org.troy.capstone.ui_components.items;

import java.util.List;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.ui_components.items.searched.SearchedItemPanel;
import org.troy.capstone.utils.UIUtils;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class RecentlyViewedWindow extends VBox{

    private final VBox scrollPaneContent;

    public static RecentlyViewedWindow create() {
        RecentlyViewedWindow window = new RecentlyViewedWindow();
        Label titleLabel = new Label("Recently Viewed Items");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        window.getChildren().add(0, titleLabel); // Add title at the top of
        UIUtils.setSize(window, UISizeControl.RECENTLY_VIEWED_WINDOW_WIDTH.getValue(), UISizeControl.RECENTLY_VIEWED_WINDOW_HEIGHT.getValue());
        return window;
    }

    private RecentlyViewedWindow(){
        scrollPaneContent = new VBox(UISizeControl.HEIGHT_PADDING.getValue());
        scrollPaneContent.getChildren().add(new Label("No recently viewed items."));
        ScrollPane scrollPane = new ScrollPane(scrollPaneContent);
        getChildren().add(scrollPane);
    }

    public void setContent(List<SearchedItemPanel> panels) {
        scrollPaneContent.getChildren().clear();
        scrollPaneContent.getChildren().addAll(panels);
    }

}
