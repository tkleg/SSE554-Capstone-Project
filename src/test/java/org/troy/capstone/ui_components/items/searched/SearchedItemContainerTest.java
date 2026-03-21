package org.troy.capstone.ui_components.items.searched;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.entities.Item;

import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.VBox;

public class SearchedItemContainerTest {
    private SearchedItemContainer container;

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() {
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() {
        container = SearchedItemContainer.create(List.of(Item.randomItem()));
    }

    @Test
    @DisplayName("Test SearchedItemContainer creation and successful item panel addition")
    public void successfulSearchedItemContainerCreation() {
        assertNotNull(container, "SearchedItemContainer should be created successfully");
        
        Field itemContainerField;
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

        //Verify that the item panel was added to the container
        assertEquals(1, itemContainer.getChildren().size(), "Container should have exactly one item panel after addition");
    }

    @Test
    @DisplayName("Test adding null item panel does not throw exception and does not add to container")
    public void testAddNullItemPanel() {
        assertNotNull(container, "SearchedItemContainer should be created successfully");
        
        Method addItemPanelMethod;
        
        Field itemContainerField;
        VBox itemContainer;
        try{
            addItemPanelMethod = SearchedItemContainer.class.getDeclaredMethod("addItemPanel", SearchedItemPanel.class);
            addItemPanelMethod.setAccessible(true);
            addItemPanelMethod.invoke(container, (SearchedItemPanel) null);

            itemContainerField = SearchedItemContainer.class.getDeclaredField("itemContainer");
            itemContainerField.setAccessible(true);
            Object itemContainerObj = itemContainerField.get(container);
            assert itemContainerObj instanceof VBox : "Expected itemContainer to be a VBox, but got: " + itemContainerObj.getClass();
            itemContainer = (VBox) itemContainerObj;
        } catch ( NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to access itemContainer field via reflection", e);
        }
        //Verify that no item panel was added to the container
        assertEquals(1, itemContainer.getChildren().size() , "Container should only have the original item panel and add no others when null is passed to addItemPanel");
    }

}
