package org.troy.capstone.managers;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.data_structures.RecentlyViewedQueue;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.interfaces.SearchedItemPanelSourceUI;
import org.troy.capstone.ui_components.items.RecentlyViewedWindow;
import org.troy.capstone.ui_components.items.searched.SearchedItemPagination;

import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.VBox;
import tech.tablesaw.api.Table;

//The RecentlyViewedWindow tests most of this class already, so here we just test the duplicate item handling, which is the main logic in this class that isn't already tested by RecentlyViewedWindowTest.
public class RecentlyViewedManagerTest {

    private static final Table table = TestDataHolder.getTableCopy();
    private static final ItemHashMap itemHashMap = TestDataHolder.getItemHashMapCopy();
    private static RecentlyViewedWindow recentlyViewedWindow;
    private static RecentlyViewedManager recentlyViewedManager;
    private static SearchedItemPagination searchedItemPagination;
    private static Field contentField;
    private static RecentlyViewedQueue recentlyViewedQueue;
    private static VBox content;

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() throws ReflectiveOperationException {
        new JFXPanel();

        contentField = RecentlyViewedWindow.class.getDeclaredField("content");
        contentField.setAccessible(true);

        recentlyViewedWindow = RecentlyViewedWindow.create();
        searchedItemPagination = new SearchedItemPagination(itemHashMap);
        recentlyViewedManager = RecentlyViewedManager.create(itemHashMap, recentlyViewedWindow, (SearchedItemPanelSourceUI) searchedItemPagination);

        Field recentlyViewedQueueField = RecentlyViewedManager.class.getDeclaredField("recentlyViewedQueue");
        recentlyViewedQueueField.setAccessible(true);
        recentlyViewedQueue = (RecentlyViewedQueue) recentlyViewedQueueField.get(recentlyViewedManager);

    }

    @BeforeEach
    public void clearRecentlyViewedWindow() throws IllegalArgumentException, ReflectiveOperationException {
        content = (VBox) contentField.get(recentlyViewedWindow);
        content.getChildren().clear();
        recentlyViewedQueue.clear();
    }

    
    @Test
    @DisplayName("Test addRecentlyViewedItem with duplicate item IDs")
    public void testAddRecentlyViewedItemWithDuplicate() throws ReflectiveOperationException {
        String firstId = table.row(0).getString(TableColumnName.ID.getColumnName());
        for( int x = 0; x < 5; x++)
            recentlyViewedManager.onItemSelected(firstId);

        assert content.getChildren().size() == 1 : "Expected 1 recently viewed item, but got " + content.getChildren().size();
    }

    @Test
    @DisplayName("Test addRecentlyViewedItem with multiple different item IDs including duplicates")
    public void testAddRecentlyViewedItemWithMultipleDifferentIdsAndDuplicates() throws ReflectiveOperationException {
        String firstId = table.row(0).getString(TableColumnName.ID.getColumnName());
        String secondId = table.row(1).getString(TableColumnName.ID.getColumnName());
        recentlyViewedManager.onItemSelected(firstId);
        recentlyViewedManager.onItemSelected(secondId);
        recentlyViewedManager.onItemSelected(firstId);

        assert content.getChildren().size() == 2 : "Expected 2 recently viewed items, but got " + content.getChildren().size();
    }
}