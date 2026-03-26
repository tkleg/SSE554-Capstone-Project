package org.troy.capstone.ui_components.items;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.entities.Item;
import org.troy.capstone.managers.RecentlyViewedManager;

import javafx.embed.swing.JFXPanel;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AttributedItemContainerTest {
    private static Item item;
    private AttributedItemContainer attributedItemContainer;
    private static Method makeAttributionFlowTest;
    private RecentlyViewedManager mockRecentlyViewedManager;
    
    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() throws NoSuchMethodException {
        new JFXPanel();
        item = Item.fromRow(TestDataHolder.getTableCopy().row(0));
        makeAttributionFlowTest = AttributedItemContainer.class.getDeclaredMethod("makeAttributionFlow", Item.class);
        makeAttributionFlowTest.setAccessible(true);
    }

    @BeforeEach
    public void setUp() {
        mockRecentlyViewedManager = Mockito.mock(RecentlyViewedManager.class);
        //Mockito.doNothing().when(mockRecentlyViewedManager).addRecentlyViewedItem(Mockito.anyString());
        attributedItemContainer = new AttributedItemContainer(item, mockRecentlyViewedManager);
    }

    @Test
    @DisplayName("Test makeAttributionFlow with valid item")
    public void testMakeAttributionFlow() throws Exception {
        TextFlow flow = (TextFlow) makeAttributionFlowTest.invoke(attributedItemContainer, item);
        assert flow.getChildren().size() == 4 : "Expected 4 children in the attribution flow, but got: " + flow.getChildren().size();
        String fullText = flow.getChildren().stream()
            .map(node -> ((Text) node).getText())
            .reduce(String::concat).orElse("Cannot concatenate text from flow");
        assert fullText.equals( "Photo by " + item.getPhotoAuthor() + " on Unsplash" ) : "Expected full attribution text to be 'Photo by " + item.getPhotoAuthor() + " on Unsplash', but got: '" + fullText + "'";
    }

}
