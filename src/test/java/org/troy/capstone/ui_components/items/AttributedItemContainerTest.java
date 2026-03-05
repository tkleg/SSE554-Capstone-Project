package org.troy.capstone.ui_components.items;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.troy.capstone.constants.URL;
import org.troy.capstone.entities.Item;
import org.troy.capstone.utils.TableUtils;

import javafx.embed.swing.JFXPanel;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import static org.mockito.ArgumentMatchers.any;

import java.awt.Desktop;
import java.net.URI;

public class AttributedItemContainerTest {
    private static Item item;
    private AttributedItemContainer attributedItemContainer;

    @BeforeAll
    public static void setup() {
        new JFXPanel();
        item = Item.fromRow(TableUtils.readCleanedAttributedData().row(0));
    }

    @BeforeEach
    public void setUp() {
        attributedItemContainer = AttributedItemContainer.createFromItem(item);
    }

    @Test
    @DisplayName("Test makeAttributionFlow with valid item")
    public void testMakeAttributionFlow() {
        TextFlow flow = attributedItemContainer.makeAttributionFlow(item);
        assert flow.getChildren().size() == 4 : "Expected 4 children in the attribution flow, but got: " + flow.getChildren().size();
        String fullText = flow.getChildren().stream()
            .map(node -> ((Text) node).getText())
            .reduce(String::concat).orElse("Cannot concatenate text from flow");
        assert fullText.equals( "Photo by " + item.getPhotoAuthor() + " on Unsplash" ) : "Expected full attribution text to be 'Photo by " + item.getPhotoAuthor() + " on Unsplash', but got: '" + fullText + "'";
    }

    @Nested
    @DisplayName("Test link clicking with mocked Desktop")
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
        public void testLinkClickingWithMock(String scenario) {
            try{
                TextFlow flow = attributedItemContainer.makeAttributionFlow(item);
                
                Text authorName = (Text) flow.getChildren().get(1); // Author name
                Text sourceName = (Text) flow.getChildren().get(3); // "Unsplash"
                ImageView imageView = attributedItemContainer.getImageView(); // ImageView from the container

                //Used to get both branches of the link clicking code covered
                if( scenario.equals("failure") )
                    Mockito.doThrow(new RuntimeException("Mocked browse exception"))
                           .when(mockDesktop).browse(any(URI.class));
                else
                    Mockito.doNothing().when(mockDesktop).browse(any(URI.class));

                MouseEvent clickEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, 
                    null, 1, false, false, false, false, false, false, false, false, false, false, null);
                
                authorName.fireEvent(clickEvent);
                sourceName.fireEvent(clickEvent);
                imageView.fireEvent(clickEvent);
                
                //Verify each specific URL was called once
                Mockito.verify(mockDesktop, Mockito.times(1)).browse(new URI(item.getPhotoAuthorUrl()));
                Mockito.verify(mockDesktop, Mockito.times(1)).browse(new URI(URL.UNSPLASH_ATTRIBUTION.getUrl()));
                Mockito.verify(mockDesktop, Mockito.times(1)).browse(new URI(item.getImageUrl()));
            }catch (Exception e) {
                assert false : "Expected no exception to be thrown, but got: " + e.getMessage();
            }finally {
                desktopMock.close();
            }
            assert true;
        }
    }
}
