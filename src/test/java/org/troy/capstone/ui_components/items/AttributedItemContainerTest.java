package org.troy.capstone.ui_components.items;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.URL;
import org.troy.capstone.entities.Item;
import org.troy.capstone.managers.RecentlyViewedManager;

import javafx.embed.swing.JFXPanel;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

    @Nested
    @DisplayName("Test link clicking with mocked Desktop")
    @SuppressWarnings("unused")
    class MockedDesktopTests {
        MockedStatic<Desktop> desktopMock;
        Desktop mockDesktop;
            
        @BeforeEach
        public void setupMock() {
            desktopMock = Mockito.mockStatic(Desktop.class);
            mockDesktop = Mockito.mock(Desktop.class);
            desktopMock.when(Desktop::getDesktop).thenReturn(mockDesktop);
        }

        @ParameterizedTest
        @ValueSource(strings = {"success", "failure"})
        @DisplayName("Test link clicking author and source links with mocked Desktop")
        public void testLinkClickingWithMock(String scenario) throws Exception {
            try{
                TextFlow flow = (TextFlow) makeAttributionFlowTest.invoke(attributedItemContainer, item);
                
                Text authorName = (Text) flow.getChildren().get(1); // Author name
                Text sourceName = (Text) flow.getChildren().get(3); // "Unsplash"
                ImageView imageView = attributedItemContainer.getImageView(); // ImageView from the container

                if (scenario.equals("failure")) {
                    Mockito.doThrow(new RuntimeException("Mocked browse exception"))
                           .when(mockDesktop).browse(any(URI.class));
                } else {
                    Mockito.doNothing().when(mockDesktop).browse(any(URI.class));
                }

                MouseEvent clickEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, 
                    null, 1, false, false, false, false, false, false, false, false, false, false, null);

                //Simulate clicking the author name, source name, and image
                boolean authorNameThrows = false;
                try{
                    authorName.fireEvent(clickEvent);
                }catch (RuntimeException e) {
                    authorNameThrows = true;
                    assert e.getMessage().equals("Mocked browse exception") : "Expected exception message to be 'Mocked browse exception', but got: " + e.getMessage();
                }
                boolean sourceNameThrows = false;
                try{
                    sourceName.fireEvent(clickEvent);
                }catch (RuntimeException e) {
                    sourceNameThrows = true;
                    assert e.getMessage().equals("Mocked browse exception") : "Expected exception message to be 'Mocked browse exception', but got: " + e.getMessage();
                }
                boolean imageViewThrows = false;
                try{
                    imageView.fireEvent(clickEvent);
                }catch (RuntimeException e) {
                    imageViewThrows = true;
                    assert e.getMessage().equals("Mocked browse exception") : "Expected exception message to be 'Mocked browse exception', but got: " + e.getMessage();
                }
                
                //Verify each specific URL was called once
                Mockito.verify(mockDesktop, Mockito.times(1)).browse(new URI(item.getPhotoAuthorUrl()));
                Mockito.verify(mockDesktop, Mockito.times(1)).browse(new URI(URL.UNSPLASH_ATTRIBUTION.getUrl()));
                Mockito.verify(mockDesktop, Mockito.times(1)).browse(new URI(item.getImageUrl()));
            }catch (IOException | URISyntaxException e) {
                assert false : "Expected no exception to be thrown, but got: " + e.getMessage();
            }finally {
                desktopMock.close();
            }
            assert true;
        }
    }
}
