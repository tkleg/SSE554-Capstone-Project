package org.troy.capstone.data_structures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.managers.RecentlyViewedManager;
import org.troy.capstone.ui_components.items.searched.SearchedItemPanel;

public class RecentlyViewedQueue extends ArrayBlockingQueue<SearchedItemPanel>{
    private static final int CAPACITY = 10;

    private final ItemHashMap itemHashMap;

    /** Queue to keep track of item IDs for quick lookup and to prevent duplicates. Faster than using SearchedItemPanel directly since a lot of work is done to create a panel before checking. */
    private final ArrayBlockingQueue<String> itemIds;

    /** Reference to the manager for recently viewed items, used to update the recently viewed items window when interacting with the queue. */
    private final RecentlyViewedManager recentlyViewedManager;

    public RecentlyViewedQueue(ItemHashMap itemHashMap, RecentlyViewedManager recentlyViewedManager) {
        super(CAPACITY);
        itemIds = new ArrayBlockingQueue<>(CAPACITY);
        this.itemHashMap = itemHashMap;
        this.recentlyViewedManager = recentlyViewedManager;
    }
    
    public boolean addAttempt(String itemId) {
        if (itemIds.contains(itemId)){
            System.out.println("Item with ID " + itemId + " is already in the recently viewed queue. Not adding again.");
            return false; //Item already in the queue, do not add again
        }
        if (itemIds.remainingCapacity() == 0) {
            itemIds.poll(); // Remove the oldest item ID
            poll(); // Remove the oldest item panel
            System.out.println("Recently viewed queue is full. Oldest item removed to make space for new item.");
        }
        add(itemId);
        System.out.println("Item with ID " + itemId + " added to recently viewed queue.");
        return true;
    }

    private void add(String itemId) {
        itemIds.add(itemId);
        add(new SearchedItemPanel(
            itemHashMap.getItem(itemId).orElseThrow(),
            recentlyViewedManager
        ));
    }

    /**
     * Returns a List containing all items in the queue in order, without removing them.
     */
    public List<SearchedItemPanel> peekAll() {
        return new ArrayList<>(this);
    }
    
}
