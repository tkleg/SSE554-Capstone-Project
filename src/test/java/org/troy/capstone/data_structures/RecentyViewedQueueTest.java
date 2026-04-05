package org.troy.capstone.data_structures;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.ui_components.items.SearchedItemPanel;

import javafx.embed.swing.JFXPanel;
import tech.tablesaw.api.Table;

public class RecentyViewedQueueTest {
    private static final Table table = TestDataHolder.getTableCopy();
    private static final ItemHashMap itemHashMap = TestDataHolder.getItemHashMapCopy();
    private static final RecentlyViewedQueue recentlyViewedQueue = new RecentlyViewedQueue(itemHashMap);


    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() {
        new JFXPanel();
    }

    @BeforeEach
    public void clearQueue() {
        recentlyViewedQueue.clear();
    }

    @Test
    @DisplayName("Test adding one item to the recently viewed queue")
    public void testAddOneItem() {
        String itemId = table.row(0).getString(TableColumnName.ID.getColumnName());
        recentlyViewedQueue.addAttempt(itemId);
        assert recentlyViewedQueue.peekAll().stream()
            .map(SearchedItemPanel::getItemId).collect(Collectors.toSet()).equals(Set.of(itemId))
            : "Expected recently viewed queue to contain the added item ID, but it was not found.";
        assert recentlyViewedQueue.size() == 1 : "Expected recently viewed queue size to be 1 after adding one item, but got: " + recentlyViewedQueue.size();
    }

    @Test
    @DisplayName("Test adding multiple items to the recently viewed queue")
    public void testAddMultipleItems() {
        String itemId1 = table.row(0).getString(TableColumnName.ID.getColumnName());
        String itemId2 = table.row(1).getString(TableColumnName.ID.getColumnName());
        recentlyViewedQueue.addAttempt(itemId1);
        recentlyViewedQueue.addAttempt(itemId2);
        assert recentlyViewedQueue.peekAll().stream()
            .map(SearchedItemPanel::getItemId).collect(Collectors.toSet()).equals(Set.of(itemId1, itemId2))
            : "Expected recently viewed queue to contain the added item IDs, but they were not found.";
        assert recentlyViewedQueue.size() == 2 : "Expected recently viewed queue size to be 2 after adding two items, but got: " + recentlyViewedQueue.size();
    }

    @Test
    @DisplayName("Test adding duplicate items to the recently viewed queue")
    public void testAddDuplicateItems() {
        String itemId = table.row(0).getString(TableColumnName.ID.getColumnName());
        recentlyViewedQueue.addAttempt(itemId);
        recentlyViewedQueue.addAttempt(itemId);
        assert recentlyViewedQueue.peekAll().stream()
            .map(SearchedItemPanel::getItemId).collect(Collectors.toSet()).equals(Set.of(itemId))
            : "Expected recently viewed queue to contain the added item ID, but it was not found.";
        assert recentlyViewedQueue.size() == 1 : "Expected recently viewed queue size to remain 1 after adding a duplicate item, but got: " + recentlyViewedQueue.size();
    }

}
