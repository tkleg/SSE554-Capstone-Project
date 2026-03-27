package org.troy.capstone.managers;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.ui_components.items.RecentlyViewedWindow;

import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.VBox;
import tech.tablesaw.api.Table;

//The RecentlyViewedWindow tests most of this class already, so here we just test the duplicate item handling, which is the main logic in this class that isn't already tested by RecentlyViewedWindowTest.
public class RecentlyViewedManagerTest {

    private static Table table;

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() {
        new JFXPanel();
        table = TestDataHolder.getTableCopy();
    }

    @Test
    public void testAddRecentlyViewedItemWithDuplicate() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        RecentlyViewedWindow window = RecentlyViewedWindow.create();
        RecentlyViewedManager manager = new RecentlyViewedManager(TestDataHolder.getItemHashMapCopy(), window);
        String firstId = table.row(0).getString(TableColumnName.ID.getColumnName());
        for( int x = 0; x < 5; x++)
            manager.addRecentlyViewedItem(firstId);

        Field scrollPaneContentField = RecentlyViewedWindow.class.getDeclaredField("scrollPaneContent");
        scrollPaneContentField.setAccessible(true);
        VBox scrollPaneContent = (VBox) scrollPaneContentField.get(window);

        assert scrollPaneContent.getChildren().size() == 1 : "Expected 1 recently viewed item, but got " + scrollPaneContent.getChildren().size();
    }
}
