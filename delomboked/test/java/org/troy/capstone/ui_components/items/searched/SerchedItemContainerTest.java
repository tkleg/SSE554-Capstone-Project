package org.troy.capstone.ui_components.items.searched;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.entities.Item;

import javafx.scene.layout.VBox;
import javafx.embed.swing.JFXPanel;

import java.lang.reflect.Field;

public class SerchedItemContainerTest {
    private SearchedItemContainer container;

    @BeforeAll
    public static void setup() {
        new JFXPanel();
    }

    @BeforeEach
    public void setUp() {
        container = new SearchedItemContainer();
    }

    @Test
    @DisplayName("Test SearchedItemContainer creation and successful item panel addition")
    public void successfulSearchedItemContainerCreation() {
        assertNotNull(container, "SearchedItemContainer should be created successfully");
        
        //Create a dummy item panel and add it to the container
        SearchedItemPanel dummyPanel = new SearchedItemPanel(Item.randomItem());
        container.addItemPanel(dummyPanel);
        
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
        assertTrue(itemContainer.getChildren().contains(dummyPanel), "SearchedItemPanel should be added to the container");
        assertEquals(1, itemContainer.getChildren().size(), "Container should have exactly one item panel after addition");
    }

    @Test
    @DisplayName("Test adding null item panel does not throw exception and does not add to container")
    public void testAddNullItemPanel() {
        assertNotNull(container, "SearchedItemContainer should be created successfully");
        
        //Attempt to add a null item panel
        container.addItemPanel(null);
        
        Field itemContainerField;
        VBox itemContainer;
        try{
            itemContainerField = SearchedItemContainer.class.getDeclaredField("itemContainer");
            itemContainerField.setAccessible(true);
            Object itemContainerObj = itemContainerField.get(container);
            assert itemContainerObj instanceof VBox : "Expected itemContainer to be a VBox, but got: " + itemContainerObj.getClass();
            itemContainer = (VBox) itemContainerObj;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access itemContainer field via reflection", e);
        }
        //Verify that no item panel was added to the container
        assertTrue(itemContainer.getChildren().isEmpty(), "Container should remain empty when adding null item panel");
    }

}
