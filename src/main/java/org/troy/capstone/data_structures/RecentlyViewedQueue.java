package org.troy.capstone.data_structures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.troy.capstone.constants.MiscValues;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.ui_components.items.SearchedItemPanel;

/**
 * Queue to manage recently viewed items, with a fixed capacity. Oldest item is removed when capacity is exceeded. Does not allow duplicates.
 */
public class RecentlyViewedQueue extends ArrayBlockingQueue<SearchedItemPanel>{
    /** Capacity of the recently viewed queue, set to 10 for a reasonable number of items to display without overwhelming the user. */

    /** HashMap to store item details for quick access when creating SearchedItemPanel instances. */
    private final ItemHashMap itemHashMap;

    /** Queue to keep track of item IDs for quick lookup and to prevent duplicates. Faster than using SearchedItemPanel directly since a lot of work is done to create a panel before checking. */
    private final ArrayBlockingQueue<String> itemIds;

    /** Constructor for the RecentlyViewedQueue.
     * @param itemHashMap The ItemHashMap to use for retrieving item details.
     */
    public RecentlyViewedQueue(ItemHashMap itemHashMap) {
        super(MiscValues.RECENTLY_VIEWED_QUEUE_SIZE.getIntValue());
        itemIds = new ArrayBlockingQueue<>(MiscValues.RECENTLY_VIEWED_QUEUE_SIZE.getIntValue());
        this.itemHashMap = itemHashMap;
    }
    
    /** Attempts to add an item to the recently viewed queue.
     * 
     * @pre itemId is not null and corresponds to a valid key in the itemHashMap.
     * @post If the item is not already in the queue, it is added and the oldest item is removed. If the item is already in the queue, it is moved to the top (most recent position) without duplication. If the queue is full, the oldest item is removed to make space for the new item.
     * @param itemId The ID of the item to add.
     */
    public void addAttempt(String itemId) {
        if (itemIds.contains(itemId)) {
            //Remove the itemId and its corresponding panel, then re-add to the end (top)
            itemIds.remove(itemId);
            //Remove the corresponding SearchedItemPanel
            removeIf(panel -> panel.getItemId().equals(itemId));
            System.out.println("Item with ID " + itemId + " is already in the recently viewed queue. Moving to top.");
        } else if (itemIds.remainingCapacity() == 0) {
            itemIds.poll(); // Remove the oldest item ID
            poll(); // Remove the oldest item panel
            System.out.println("Recently viewed queue is full. Oldest item removed to make space for new item.");
        }
        add(itemId);
        System.out.println("Item with ID " + itemId + " added to recently viewed queue.");
    }

    /** 
     * Adds an item to the recently viewed queue without checking for duplicates or capacity. Should only be called from addAttempt after those checks have been made.
     * @pre itemId is not null and is the key of an item allowed in the queue based on checks in addAttempt.
     * @post The item with the given ID is added to the queue as a SearchedItemPanel.
     * @param itemId The ID of the item to add.
     */
    private void add(String itemId) {
        itemIds.add(itemId);
        add(SearchedItemPanel.create(
            itemHashMap.getItem(itemId).orElseThrow()
        ));
    }

    /**
     * Returns a List containing all items in the queue in reverse order, without removing them.
     * @return A List of SearchedItemPanel objects representing the items in the queue, in order from newest to oldest.
     */
    public List<SearchedItemPanel> peekAll() {
        return new ArrayList<>(this).reversed();
    }
    
}