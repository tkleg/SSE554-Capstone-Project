package org.troy.capstone.ui_components.items.searched;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.entities.Item;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SearchedItemContainerTest {
    private SearchedItemContainer container;
    private static Field itemContainerField;

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() {
        new JFXPanel();
        try {
            itemContainerField = SearchedItemContainer.class.getDeclaredField("itemContainer");
            itemContainerField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to access itemContainer field via reflection", e);
        }
    }

    @BeforeEach
    public void setUp() {
        container = SearchedItemContainer.create(List.of(Item.randomItem()), null);
    }

    @Test
    @DisplayName("Test SearchedItemContainer creation and successful item panel addition")
    public void successfulSearchedItemContainerCreation() {
        assertNotNull(container, "SearchedItemContainer should be created successfully");
        
        VBox itemContainer;
        try {
            Object itemContainerObj = itemContainerField.get(container);
            assert itemContainerObj instanceof VBox : "Expected itemContainer to be a VBox, but got: " + itemContainerObj.getClass();
            itemContainer = (VBox) itemContainerObj;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access itemContainer field via reflection", e);
        }

        //Verify that the item panel was added to the container
        assertEquals(1, itemContainer.getChildren().size(), "Container should have exactly one item panel after addition");
    }

    @Test
    @DisplayName("Test adding null item panel does not throw exception and does not add to container")
    public void testAddNullItemPanel() {
        assertNotNull(container, "SearchedItemContainer should be created successfully");
        
        Method addItemPanelMethod;
        
        VBox itemContainer;
        try{
            addItemPanelMethod = SearchedItemContainer.class.getDeclaredMethod("addItemPanel", SearchedItemPanel.class);
            addItemPanelMethod.setAccessible(true);
            addItemPanelMethod.invoke(container, (SearchedItemPanel) null);

            Object itemContainerObj = itemContainerField.get(container);
            assert itemContainerObj instanceof VBox : "Expected itemContainer to be a VBox, but got: " + itemContainerObj.getClass();
            itemContainer = (VBox) itemContainerObj;
        } catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to access itemContainer field via reflection", e);
        }
        //Verify that no item panel was added to the container
        assertEquals(1, itemContainer.getChildren().size() , "Container should only have the original item panel and add no others when null is passed to addItemPanel");
    }

    @Test
    @DisplayName("Test updateItems with empty list")
    public void testUpdateItemsWithEmptyList() throws Exception {
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                container.updateItems(List.of());
                VBox itemContainer;
                try {
                    itemContainerField = SearchedItemContainer.class.getDeclaredField("itemContainer");
                    itemContainerField.setAccessible(true);
                    Object itemContainerObj = itemContainerField.get(container);
                    assert itemContainerObj instanceof VBox : "Expected itemContainer to be a VBox, but got: " + itemContainerObj.getClass();
                    itemContainer = (VBox) itemContainerObj;
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("Failed to access itemContainer field via reflection", e);
                }
                //Verify that the container shows the "No items found." message when updated with an empty list
                assertEquals(1, itemContainer.getChildren().size(), "Container should have exactly one child when updated with an empty list");
                assert itemContainer.getChildren().get(0) instanceof Label : "Expected child to be a Label, but got: " + itemContainer.getChildren().get(0).getClass();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    @DisplayName("Test updateItems with null list")
    public void testUpdateItemsWithNullList() throws Exception {
        CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                container.updateItems(List.of(Item.randomItem(), Item.randomItem()));
                container.updateItems(null);
                VBox itemContainer;
                try {
                    itemContainerField = SearchedItemContainer.class.getDeclaredField("itemContainer");
                    itemContainerField.setAccessible(true);
                    Object itemContainerObj = itemContainerField.get(container);
                    assert itemContainerObj instanceof VBox : "Expected itemContainer to be a VBox, but got: " + itemContainerObj.getClass();
                    itemContainer = (VBox) itemContainerObj;
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("Failed to access itemContainer field via reflection", e);
                }
                //On null list, the container does nothing
                assertEquals(2, itemContainer.getChildren().size(), "Container should not change the number of children when updated with a null list, but got: " + itemContainer.getChildren().size());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    @DisplayName("Test updateItems with an item that is null in the list")
    public void testUpdateItemsWithNullItemInList() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try{
                container.updateItems( Arrays.asList((Item) null));
                VBox itemContainer;
                try {
                    itemContainerField = SearchedItemContainer.class.getDeclaredField("itemContainer");
                    itemContainerField.setAccessible(true);
                    Object itemContainerObj = itemContainerField.get(container);
                    assert itemContainerObj instanceof VBox : "Expected itemContainer to be a VBox, but got: " + itemContainerObj.getClass();
                    itemContainer = (VBox) itemContainerObj;
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("Failed to access itemContainer field via reflection", e);
                }
                //Verify that the container shows the "No items found." message when updated with a list containing a null item
                assertEquals(0, itemContainer.getChildren().size(), "Container should have exactly zero children when updated with a list containing a null item");
            } finally {               
                latch.countDown();
            }
        });
        latch.await();
    }
    
}