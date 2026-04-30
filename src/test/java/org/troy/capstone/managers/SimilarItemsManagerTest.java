package org.troy.capstone.managers;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.Config;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.data_structures.SimilarItemsGraph;
import org.troy.capstone.ui_components.items.SimilarItemsContainer;
import org.troy.capstone.ui_components.items.searched.SearchedItemPagination;

import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.HBox;

public class SimilarItemsManagerTest {

    private static SimilarItemsManager similarItemsManager;

    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    @BeforeAll
    public static void setup() {
        Config.graphBuildingEnabled = true;
        new JFXPanel();
        similarItemsManager = SimilarItemsManager.create(TestDataHolder.getItemHashMapCopy(), TestDataHolder.getTableCopy(), SimilarItemsContainer.create(), new SearchedItemPagination(TestDataHolder.getItemHashMapCopy()));
        Config.graphBuildingEnabled = false;
    }
    
    @Test
    @DisplayName("Test that on item selected gets the right items from the origin and puts the panels in the destination")
    public void testOnItemSelected() throws ReflectiveOperationException {

        similarItemsManager.onItemSelected("q^Gbd:\\*|Z2S");

        Field similarItemsContainerField = SimilarItemsManager.class.getDeclaredField("similarItemsContainer");
        similarItemsContainerField.setAccessible(true);
        SimilarItemsContainer similarItemsContainerInTest = (SimilarItemsContainer) similarItemsContainerField.get(similarItemsManager);

        Field contentField = SimilarItemsContainer.class.getDeclaredField("content");
        contentField.setAccessible(true);

        HBox content = (HBox) contentField.get(similarItemsContainerInTest);

        Field numSimilarItemsToDisplayField = SimilarItemsGraph.class.getDeclaredField("NUM_SIMILAR_ITEMS_TO_DISPLAY");
        numSimilarItemsToDisplayField.setAccessible(true);
        int numSimilarItemsToDisplay = (int) numSimilarItemsToDisplayField.get(null);
        
        assert content.getChildren().size() == numSimilarItemsToDisplay :
            "Expected " + numSimilarItemsToDisplay + " similar items, but got " + content.getChildren().size();
    
    }

    @Test
    public void testThatOnItemSelectedWithInvalidIdDoesThrow() {
        assertThrows(RuntimeException.class,
            () -> similarItemsManager.onItemSelected(""),
            "Expected onItemSelected to throw a RuntimeException when given an invalid item ID, but it did not."
        );
    }

    

}
