package org.troy.capstone.ui_components.items.searched;

import java.util.ArrayList;
import java.util.List;

import org.troy.capstone.constants.UISizeControl;
import org.troy.capstone.constants.TestFXId;
import org.troy.capstone.entities.Item;
import org.troy.capstone.interfaces.SearchedItemPanelInteractor;
import org.troy.capstone.ui_components.items.SearchedItemPanel;
import org.troy.capstone.utils.UIUtils;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * The {@code SearchedItemContainer} class represents a UI component that contains and displays the search results as a list of {@code SearchedItemPanel} instances. It is a scrollable container that allows users to view all search results, and it provides methods to add new search result panels to the container.
 */
public class SearchedItemContainer extends ScrollPane {
    /** The container for all searched item panels */
    private final VBox itemContainer;

    /** List of interactors to handle interactions with the item panels in the search results. */
    private final List<SearchedItemPanelInteractor> interactors = new ArrayList<>();

    /**
     * Creates a {@code SearchedItemContainer} with a vertical box layout for displaying search result panels.
     */
    private SearchedItemContainer() {
        super();

        itemContainer = new VBox(UISizeControl.SEARCHED_ITEM_PANEL_SPACING.getValue()); // 5px spacing between items
        itemContainer.setAlignment(Pos.TOP_CENTER); // Center-align items consistently
        setContent(itemContainer);
        setFitToWidth(true);

        //Optimize scroll performance
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setPannable(false);
        
        //Cache nodes to improve scroll performance
        itemContainer.setCache(true);
        itemContainer.setCacheHint(javafx.scene.CacheHint.SPEED);

        itemContainer.setId(TestFXId.SEARCHED_ITEM_CONTAINER_CONTAINER.getId());

    }

    /** Factory method to create a {@code SearchedItemContainer} with the given list of items.
     * @param items The list of items to display in the container.
     * @return A new instance of {@code SearchedItemContainer} populated with the given items.
     */
    public static SearchedItemContainer create(List<Item> items) {
        SearchedItemContainer container = new SearchedItemContainer();
        UIUtils.setSize(container, UISizeControl.SEARCHED_ITEM_CONTAINER_WIDTH.getValue(), UISizeControl.SEARCHED_ITEM_CONTAINER_HEIGHT.getValue());
        UIUtils.setLineBorder(container, 5, 1);
        container.updateItems(items);
        return container;
    }

    /**
     * Adds a new {@code SearchedItemPanel} to the container.
     * 
     * @pre itemPanel is not null and is properly initialized with the data to display for a search result.
     * 
     * @post If itemPanel is not null, it is added to the itemContainer and becomes visible in the UI.
     * @param itemPanel The {@code SearchedItemPanel} to add.
     */ 
    private void addItemPanel(SearchedItemPanel itemPanel) {
        if( itemPanel != null ){
            itemContainer.getChildren().add(itemPanel);
            interactors.forEach(interactor -> itemPanel.addSearchedItemPanelInteractor(interactor));
        }
    }

    /** Adds a {@code SearchedItemPanelInteractor} to the {@code SearchedItemContainer} to allow for interaction with the item panels in the search results.
     * @pre interactor should be properly implemented to handle interactions with the item panels, and the {@code SearchedItemContainer} should be properly initialized to allow for adding interactors.
     * @post The provided interactor is added to the {@code SearchedItemContainer}, allowing it to receive interaction events from the item panels in the search results.
     * @param interactor The {@code SearchedItemPanelInteractor} to add to the {@code SearchedItemContainer} for handling interactions with the item panels in the search results.
     */
    public void addSearchedItemPanelInteractor(SearchedItemPanelInteractor interactor) {
        interactors.add(interactor);
        //Set interactors for initial panels
        itemContainer.getChildren().stream().filter(node -> node instanceof SearchedItemPanel)
            .forEach(node -> ((SearchedItemPanel) node).addSearchedItemPanelInteractor(interactor));
    }

    /** Updates the items displayed in the container with a new list of items.
     * @pre items should be a valid list of {@code Item} objects to display in the container. The {@code SearchedItemContainer} should be properly initialized to allow for updating the displayed items.
     * @post The {@code itemContainer} is cleared and repopulated with new {@code SearchedItemPanel} instances corresponding to the provided list of items. If the list is null or empty, a message indicating that no items were found is displayed instead.
     * @param items The new list of {@code Item} objects to display in the container.
     */
    public final void updateItems(List<Item> items) {
        if( items == null ){
            System.out.println("Warning: updateItems called with null list. Doing nothing.");
        }else if (items.isEmpty()) {
            stopAllImagesLoading();
            itemContainer.getChildren().clear();
            itemContainer.getChildren().add(new Label("No items found."));
        }else{
            stopAllImagesLoading();
            itemContainer.getChildren().clear();
            items.forEach(item -> {
                if (item != null)
                    addItemPanel(SearchedItemPanel.create(item));
            });
        }
    }

    /**
     * Stops all image loading tasks from running
     * 
     * @post All asynchronous image loading tasks for the {@code SearchedItemPanel} instances currently displayed in the container are stopped, preventing any further loading of images that are not being displayed.
     */
    public void stopAllImagesLoading() {
        itemContainer.getChildren().stream().filter(node -> node instanceof SearchedItemPanel)
            .forEach(node -> ((SearchedItemPanel) node).stopLoadingImage());
    }

}
