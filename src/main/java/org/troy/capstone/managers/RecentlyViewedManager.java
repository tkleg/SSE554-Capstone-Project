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

    /** Adds an item to the recently viewed queue and updates the recently viewed window content. If the item is already in the queue, it will not be added again and a message will be printed to the console.
     * @pre itemId is not null and corresponds to a valid key in the itemHashMap used to create the recentlyViewedQueue.
     * @post If the item is not already in the queue, it is added and the recently viewed window content is updated to reflect the new queue state. If the added item exists already, nothing happens except a message is printed to the console.
     * @param itemId The ID of the item to add to recently viewed.
     */
    public void addRecentlyViewedItem(String itemId) {
        boolean success = recentlyViewedQueue.addAttempt(itemId);
        if(!success){
            System.out.println("Item with ID " + itemId + " is already in the recently viewed queue.");
            return;
        }
        recentlyViewedWindow.setContent(recentlyViewedQueue.peekAll());
    }

}
