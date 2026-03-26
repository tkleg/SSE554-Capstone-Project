package org.troy.capstone.managers;

import org.troy.capstone.data_structures.RecentlyViewedQueue;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.ui_components.items.RecentlyViewedWindow;

public class RecentlyViewedManager{

    private final RecentlyViewedQueue recentlyViewedQueue;

    private final RecentlyViewedWindow recentlyViewedWindow;

    public RecentlyViewedManager(ItemHashMap itemHashMap, RecentlyViewedWindow recentlyViewedWindow) {
        recentlyViewedQueue = new RecentlyViewedQueue(itemHashMap);
        this.recentlyViewedWindow = recentlyViewedWindow;
    }

    public void addRecentlyViewedItem(String itemId) {
        boolean success = recentlyViewedQueue.addAttempt(itemId);
        if(!success){
            System.out.println("Item with ID " + itemId + " is already in the recently viewed queue.");
            return;
        }
        recentlyViewedWindow.setContent(recentlyViewedQueue.peekAll());
    }

}
