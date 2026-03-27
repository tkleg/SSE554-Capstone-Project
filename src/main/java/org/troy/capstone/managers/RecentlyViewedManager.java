package org.troy.capstone.managers;

import org.troy.capstone.data_structures.RecentlyViewedQueue;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.ui_components.items.RecentlyViewedWindow;

/**
 * Manager for recently viewed items, bridges RecentlyViewedQueue and RecentlyViewedWindow. Handles adding items to the recently viewed queue and updating the window content accordingly without the window and queue depending on each other.
 */
public class RecentlyViewedManager{

    /** Queue to manage recently viewed items. */
    private final RecentlyViewedQueue recentlyViewedQueue;

    /** Window to display recently viewed items. */
    private final RecentlyViewedWindow recentlyViewedWindow;

    /** Constructor for the RecentlyViewedManager.
     * @param itemHashMap The ItemHashMap to use for retrieving item details.
     * @param recentlyViewedWindow The RecentlyViewedWindow to use for displaying recently viewed items.
     */
    public RecentlyViewedManager(ItemHashMap itemHashMap, RecentlyViewedWindow recentlyViewedWindow) {
        recentlyViewedQueue = new RecentlyViewedQueue(itemHashMap, this);
        this.recentlyViewedWindow = recentlyViewedWindow;
    }

    /** Adds an item to the recently viewed queue and updates the recently viewed window content.
      * @pre itemId is not null and corresponds to a valid key in the itemHashMap used to create the recentlyViewedQueue.
      * @post If the item is not already in the queue, it is added and the oldest item is removed. If the added item exists already, it is moved to the top. The recently viewed window content is updated to reflect the current state of the recently viewed queue.
      * @param itemId The ID of the item to add to recently viewed.
      */
    public void addRecentlyViewedItem(String itemId) {
        recentlyViewedQueue.addAttempt(itemId);
        recentlyViewedWindow.setContent(recentlyViewedQueue.peekAll());
    }

}
