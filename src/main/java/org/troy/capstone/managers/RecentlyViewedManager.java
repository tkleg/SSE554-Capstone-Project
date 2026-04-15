package org.troy.capstone.managers;

import org.troy.capstone.data_structures.RecentlyViewedQueue;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.interfaces.SearchedItemPanelDestinationUI;
import org.troy.capstone.interfaces.SearchedItemPanelInteractor;
import org.troy.capstone.interfaces.SearchedItemPanelSourceUI;
import org.troy.capstone.ui_components.items.RecentlyViewedWindow;

/**
 * Manager for recently viewed items, bridges RecentlyViewedQueue and RecentlyViewedWindow. Handles adding items to the recently viewed queue and updating the window content accordingly without the window and queue depending on each other.
 * Implements SearchedItemPanelInteractor to listen for item selection events from the searched item panels, allowing it to update the recently viewed content based on user interactions with the search results.
 */
public class RecentlyViewedManager implements SearchedItemPanelInteractor {

    /** Queue to manage recently viewed items. */
    private final RecentlyViewedQueue recentlyViewedQueue;

    /** Window to display recently viewed items. */
    private final RecentlyViewedWindow recentlyViewedWindow;

    /** Constructor for the RecentlyViewedManager.
     * @param itemHashMap The ItemHashMap to use for retrieving item details.
     * @param recentlyViewedWindow The RecentlyViewedWindow to use for displaying recently viewed items.
     */
    private RecentlyViewedManager(ItemHashMap itemHashMap, RecentlyViewedWindow recentlyViewedWindow ) {
        recentlyViewedQueue = new RecentlyViewedQueue(itemHashMap);
        this.recentlyViewedWindow = recentlyViewedWindow;
    }

    /** Factory method to create a RecentlyViewedManager and attach it as a listener to the given SearchedItemPanelSourceUI, allowing to receive item selection events from within the SearchedItemPanelSourceUI.
     * 
     * @pre itemHashMap, recentlyViewedWindow, and panelSourceUI are not null. panelSourceUI is an instance of SearchedItemPanelSourceUI. recentlyViewedWindow is an instance of RecentlyViewedWindow.
     * @param itemHashMap The ItemHashMap to use for retrieving item details.
     * @param recentlyViewedWindow The RecentlyViewedWindow to use for displaying recently viewed items.
     * @param panelSourceUI The SearchedItemPanelSourceUI that will hold the search results, used to attach listeners to the item panels within.
     * @return A new instance of RecentlyViewedManager with the given parameters, and registered as a listener to the panelSourceUI.
     */
    static RecentlyViewedManager create(ItemHashMap itemHashMap, SearchedItemPanelDestinationUI destinationUI, SearchedItemPanelSourceUI panelSourceUI) {
        RecentlyViewedManager manager = new RecentlyViewedManager(itemHashMap, (RecentlyViewedWindow) destinationUI);
        panelSourceUI.addSearchedItemPanelInteractor(manager);
        return manager;
    }
    
    /** Called when an item is selected in the searched item panel. Adds the selected item to the recently viewed queue and updates the recently viewed window content accordingly.
     * @pre itemId is not null and corresponds to a valid key in the itemHashMap.
     * @param itemId The ID of the selected item.
     */
    @Override
    public void onItemSelected(String itemId) {
        addRecentlyViewedItem(itemId);
    }
    
    /** Adds an item to the recently viewed queue and updates the recently viewed window content.
      * @pre itemId is not null and corresponds to a valid key in the itemHashMap used to create the recentlyViewedQueue.
      * @post If the item is not already in the queue, it is added and the oldest item is removed. If the added item exists already, it is moved to the top. The recently viewed window content is updated to reflect the current state of the recently viewed queue.
      * @param itemId The ID of the item to add to recently viewed.
      */
    public void addRecentlyViewedItem(String itemId) {
        System.out.println("Adding item with ID " + itemId + " to recently viewed.");
        recentlyViewedQueue.addAttempt(itemId);
        recentlyViewedWindow.setContent(recentlyViewedQueue.peekAll());
    }

}
