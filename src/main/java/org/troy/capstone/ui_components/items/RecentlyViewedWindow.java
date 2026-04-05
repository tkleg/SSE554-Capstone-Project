package org.troy.capstone.ui_components.items;

import java.util.List;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.utils.UIUtils;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/** Window to display recently viewed items. It is a ScrollPane, which contains a VBox for the content. */
public class RecentlyViewedWindow extends ScrollPane{

    /** The VBox that holds the content of the window. */
    private final VBox content;

    /** Factory method to create a RecentlyViewedWindow instance with a title label and a scrollable content area. 
     * @return A RecentlyViewedWindow instance with a title label and an empty scrollable content area.
    */
    public static RecentlyViewedWindow create() {
        RecentlyViewedWindow window = new RecentlyViewedWindow();
        UIUtils.setSize(window, UISizeControl.RECENTLY_VIEWED_WINDOW_WIDTH.getValue(), UISizeControl.RECENTLY_VIEWED_WINDOW_HEIGHT.getValue());
        return window;
    }

    /** Private constructor to initialize the RecentlyViewedWindow.
     * @pre The RecentlyViewedWindow should be properly initialized to contain a ScrollPane with a VBox as its content.
     * @post The RecentlyViewedWindow instance is created with a ScrollPane containing an empty VBox as its content, ready to have recently viewed items added to it.
     */
    private RecentlyViewedWindow(){
        content = new VBox(UISizeControl.HEIGHT_PADDING.getValue());
        Label titleLabel = new Label("Recently Viewed Items");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        content.getChildren().add(titleLabel);
        setContent(content);
    }

    /** Sets the content of the RecentlyViewedWindow with the given list of SearchedItemPanel instances.
     * @pre The panels list should contain valid SearchedItemPanel instances to be displayed in the RecentlyViewedWindow.
     * @post The RecentlyViewedWindow's content is updated to display the provided SearchedItemPanel instances.
     * @param panels The list of SearchedItemPanel instances to be displayed in the RecentlyViewedWindow.
     */
    public void setContent(List<SearchedItemPanel> panels) {
        content.getChildren().clear();
        content.getChildren().addAll(panels);
    }

}
