package org.troy.capstone.ui_components.items;

import java.util.List;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.constants.TestFXId;
import org.troy.capstone.interfaces.SearchedItemPanelDestinationUI;
import org.troy.capstone.utils.UIUtils;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/** Window to display recently viewed items. It is a {@code ScrollPane}, which contains a {@code VBox} for the content. */
public class RecentlyViewedWindow extends ScrollPane implements SearchedItemPanelDestinationUI {

    /** The {@code VBox} that holds the content of the window. */
    private final VBox content;

    /** Factory method to create a {@code RecentlyViewedWindow} instance with a title label and a scrollable content area. 
     * @return A {@code RecentlyViewedWindow} instance with a title label and an empty scrollable content area.
    */
    public static RecentlyViewedWindow create() {
        RecentlyViewedWindow window = new RecentlyViewedWindow();
        UIUtils.setSize(window, UISizeControl.RECENTLY_VIEWED_WINDOW_WIDTH.getValue(), UISizeControl.RECENTLY_VIEWED_WINDOW_HEIGHT.getValue());
        UIUtils.setLineBorder(window, 5, 1);
        return window;
    }

    /** Private constructor to initialize the {@code RecentlyViewedWindow}.
     * @pre The {@code RecentlyViewedWindow} should be properly initialized to contain a {@code ScrollPane} with a {@code VBox} as its content.
     * @post The {@code RecentlyViewedWindow} instance is created with a {@code ScrollPane} containing an empty {@code VBox} as its content, ready to have recently viewed items added to it.
     */
    private RecentlyViewedWindow(){
        content = new VBox(UISizeControl.HEIGHT_PADDING.getValue());
        Label titleLabel = new Label("Recently Viewed Items");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        content.getChildren().add(titleLabel);
        content.setId(TestFXId.RECENTLY_VIEWED_CONTAINER.getId());
        setContent(content);
    }

    /** Sets the content of the {@code RecentlyViewedWindow} with the given list of {@code Node} instances.
     * @pre The panels list should contain valid {@code Node} instances to be displayed in the {@code RecentlyViewedWindow}.
     * @post The {@code RecentlyViewedWindow}'s content is updated to display the provided {@code Node} instances.
     * @param panels The list of {@code Node} instances to be displayed in the {@code RecentlyViewedWindow}.
     */
    @Override
    public void setContent(List<Node> panels) {
        content.getChildren().clear();
        content.getChildren().addAll(panels);
    }

}
