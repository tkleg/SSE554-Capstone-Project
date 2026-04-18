package org.troy.capstone.ui_components.items;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.interfaces.SearchedItemPanelDestinationUI;
import org.troy.capstone.interfaces.SearchedItemPanelSourceUI;
import org.troy.capstone.managers.RecentlyViewedManager;
import org.troy.capstone.ui_components.items.searched.SearchedItemPagination;

import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.VBox;
import tech.tablesaw.api.Table;

public class RecentlyViewedWindowTest {

    private static Table table;
    private static ItemHashMap itemHashMap;
    
    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() {
        new JFXPanel();
        table = TestDataHolder.getTableCopy();
        itemHashMap = TestDataHolder.getItemHashMapCopy();
    }

    @ParameterizedTest
    @CsvSource({
        "0, 1",
        "1, 1",
        "5, 5",
        "10, 10",
        "20, 10"
    })
    public void testRecentlyViewedWindow(int queueInputs, int expectedQueueSize) throws IllegalArgumentException, ReflectiveOperationException {
        RecentlyViewedWindow window = RecentlyViewedWindow.create();
        Method createManagerMethod = RecentlyViewedManager.class.getDeclaredMethod("create", ItemHashMap.class, SearchedItemPanelDestinationUI.class, SearchedItemPanelSourceUI.class);
        createManagerMethod.setAccessible(true);
        RecentlyViewedManager manager = (RecentlyViewedManager) createManagerMethod.invoke(null, itemHashMap, window, new SearchedItemPagination(itemHashMap));
        table.first(queueInputs).stringColumn(TableColumnName.ID.getColumnName())
            .forEach(manager::onItemSelected);

        Field contentField = RecentlyViewedWindow.class.getDeclaredField("content");
        contentField.setAccessible(true);
        VBox content = (VBox) contentField.get(window);
        assert content.getChildren().size() == expectedQueueSize : "Expected " + expectedQueueSize + " recently viewed items, but got " + content.getChildren().size();
        
    }
}
