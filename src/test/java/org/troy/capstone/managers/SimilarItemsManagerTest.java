package org.troy.capstone.managers;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.MiscValues;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.ui_components.items.SimilarItemsContainer;

import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.HBox;
import tech.tablesaw.api.Table;

public class SimilarItemsManagerTest {

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() {
        new JFXPanel();
    }

    @Test
    @DisplayName("Test that on item selected gets the right items from the origin and puts the panels in the destination")
    public void testOnItemSelected() throws ReflectiveOperationException {
        Table table = TestDataHolder.getTableCopy();
        SimilarItemsManager similarItemsManager = TestDataHolder.getFreshSimilarItemsManager();
        System.out.println("\n\n\n\n\n\n\n\nTesting onItemSelected with item ID: " + table.row(0).getString(TableColumnName.ID.getColumnName()));
        System.out.println("Size of table: " + table.rowCount());
        System.out.println("Size of itemHashMap: " + TestDataHolder.getItemHashMapCopy().size() + "\n\n\n\n\n\n\n\n");
        similarItemsManager.onItemSelected(table.row(1).getString(TableColumnName.ID.getColumnName()));

        Field similarItemsContainerField = SimilarItemsManager.class.getDeclaredField("similarItemsContainer");
        similarItemsContainerField.setAccessible(true);
        SimilarItemsContainer similarItemsContainerInTest = (SimilarItemsContainer) similarItemsContainerField.get(similarItemsManager);

        Field contentField = SimilarItemsContainer.class.getDeclaredField("content");
        contentField.setAccessible(true);

        HBox content = (HBox) contentField.get(similarItemsContainerInTest);
        assert content.getChildren().size() == MiscValues.NUM_SIMILAR_ITEMS_TO_DISPLAY.getIntValue() :
            "Expected " + MiscValues.NUM_SIMILAR_ITEMS_TO_DISPLAY.getIntValue() + " similar items, but got " + content.getChildren().size();
    }
}
