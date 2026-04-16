package org.troy.capstone.managers;

import java.util.List;

import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.interfaces.SearchedItemPanelDestinationUI;
import org.troy.capstone.interfaces.SearchedItemPanelInteractor;
import org.troy.capstone.interfaces.SearchedItemPanelSourceUI;
import org.troy.capstone.ui_components.items.SearchedItemPanel;
import org.troy.capstone.ui_components.items.SimilarItemsContainer;

public class SimilarItemsManager implements SearchedItemPanelInteractor {

    private final SimilarItemsContainer similarItemsContainer;

    private ItemHashMap itemHashMap;
    
    private SimilarItemsManager(SimilarItemsContainer similarItemsContainer) {
        this.similarItemsContainer = similarItemsContainer;
        //TODO: Insert graph data structure stuff
    }

     /**
      * Static factory method for creating a SimilarItemsManager instance. Also handles registering the manager as a listener to the searched item panels in the searched item pagination, allowing it to update the similar items content based on user interactions with the search results.
      * @param itemHashMap The ItemHashMap containing all items, used for retrieving items to display as similar items.
      * @param destinationUI The SearchedItemPanelDestinationUI that will display the similar items, used to update the displayed similar items when new items are added.
      * @param panelSourceUI The SearchedItemPanelSourceUI that will hold the search results, used to attach listeners to the item panels within.
      * @return A new instance of SimilarItemsManager with the given parameters, and registered as a listener to the panelSourceUI.
      */
    static SimilarItemsManager create(ItemHashMap itemHashMap, SearchedItemPanelDestinationUI destinationUI, SearchedItemPanelSourceUI panelSourceUI) {
        SimilarItemsManager manager = new SimilarItemsManager((SimilarItemsContainer) destinationUI);
        manager.itemHashMap = itemHashMap;
        panelSourceUI.addSearchedItemPanelInteractor(manager);
        return manager;
    }

    @Override
    public void onItemSelected(String itemId) {
        similarItemsContainer.setContent(List.of(SearchedItemPanel.create(itemHashMap.getItem(itemId).get())));
    }

}
