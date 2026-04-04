package org.troy.capstone.ui_components.filters;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.troy.capstone.TestDataHolder;
import org.troy.capstone.constants.UIElementName;
import org.troy.capstone.managers.GeneralManager;

import javafx.embed.swing.JFXPanel;
import javafx.scene.input.MouseEvent;
import tech.tablesaw.api.Table;

public class StarRatingFilterTest {
    private StarRatingFilter starRatingFilter;
    private static final Table table = TestDataHolder.getTableCopy();
    private GeneralManager generalManager;
    private static int MAX_STARS;
    private static String FILLED_STAR;
    private static String EMPTY_STAR;

    //Why MouseEvents do not have simpler constructors... I guess we will never know
    private MouseEvent clickEvent() {
        return new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, null, 1, false, false, false, false, false, false, false, false, false, false, null);
    }
    
    private MouseEvent hoverEnterEvent() {
        return new MouseEvent(MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0, null, 0, false, false, false, false, false, false, false, false, false, false, null);
    }
    
    private MouseEvent hoverExitEvent() {
        return new MouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, 0, 0, null, 0, false, false, false, false, false, false, false, false, false, false, null);
    }

    @BeforeAll
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public static void setup() {
        new JFXPanel();
        try{
            Field maxStarsField = StarRatingFilter.class.getDeclaredField("MAX_STARS");
            maxStarsField.setAccessible(true);
            MAX_STARS = maxStarsField.getInt(null);
            Field filledStarField = StarRatingFilter.class.getDeclaredField("FILLED_STAR");
            filledStarField.setAccessible(true);
            FILLED_STAR = (String) filledStarField.get(null);
            Field emptyStarField = StarRatingFilter.class.getDeclaredField("EMPTY_STAR");
            emptyStarField.setAccessible(true);
            EMPTY_STAR = (String) emptyStarField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access StarRatingFilter constants via reflection", e);
        }
    }

    @BeforeEach
    public void setUp() {
        generalManager = new GeneralManager(table);
        starRatingFilter = new StarRatingFilter();
        generalManager.addUIElement(UIElementName.STAR_RATING_FILTER, starRatingFilter);
    }

    @Test
    @DisplayName("Test basic getRating")
    public void testGetRating() {
        assert starRatingFilter.getRating() == 0 : "Expected initial rating to be 0, but got: " + starRatingFilter.getRating();
    }

    @Test
    @DisplayName("Test previewRating on hover")
    public void testPreviewRating() {
        starRatingFilter.previewRating(3);
        assert starRatingFilter.getRating() == 0 : "Expected rating to still be 0 after previewing, but got: " + starRatingFilter.getRating();
        for( int x = 0; x < MAX_STARS; x++) {
            String expectedStar = x < 3 ? FILLED_STAR : EMPTY_STAR;
            assert starRatingFilter.stars[x].getText().equals(expectedStar) : "Expected star " + (x+1) + " to be '" + expectedStar + "', but got: '" + starRatingFilter.stars[x].getText() + "'";
        }
    }

    @Test
    @DisplayName("Test setRating and updateDisplay")
    public void testSetRatingAndUpdateDisplay() {
        starRatingFilter.setRating(1);
        assert starRatingFilter.getRating() == 1 : "Expected rating to be 1 after setting, but got: " + starRatingFilter.getRating();
        for( int x = 0; x < MAX_STARS; x++) {
            String expectedStar = x < 1 ? FILLED_STAR : EMPTY_STAR;
            assert starRatingFilter.stars[x].getText().equals(expectedStar) : "Expected star " + (x+1) + " to be '" + expectedStar + "', but got: '" + starRatingFilter.stars[x].getText() + "'";
        }
    }

    @Test
    @DisplayName("Test clicking the same rating to clear it")
    public void testClickingSameRatingToClear() {
        starRatingFilter.setRating(2);
        assert starRatingFilter.getRating() == 2 : "Expected rating to be 2 after setting, but got: " + starRatingFilter.getRating();
        starRatingFilter.setRating(2);
        assert starRatingFilter.getRating() == 0 : "Expected rating to be cleared to 0 after clicking the same rating, but got: " + starRatingFilter.getRating();
    }

    @Test
    @DisplayName("Test mouse click event on star")
    public void testStarClick() {
        starRatingFilter.stars[2].fireEvent(clickEvent());
        assert starRatingFilter.getRating() == 3 : "Expected rating to be 3 after clicking third star";
    }

    @Test 
    @DisplayName("Test mouse hover events on star")
    public void testStarHover() {
        starRatingFilter.stars[3].fireEvent(hoverEnterEvent());
        assert starRatingFilter.stars[3].getText().equals(FILLED_STAR) : "Fourth star should be filled on hover";
        
        starRatingFilter.stars[3].fireEvent(hoverExitEvent());
        assert starRatingFilter.stars[3].getText().equals(EMPTY_STAR) : "Fourth star should be empty after hover exit";
    }

    @Test
    @DisplayName("Test that generalManager has the star rating filter and it is cast properly")
    public void testStarRatingFilterInGeneralManager(){
        assert generalManager.getUIElement(UIElementName.STAR_RATING_FILTER).isPresent() : "Expected generalManager to have a STAR_RATING_FILTER element, but it was not found.";
        assert generalManager.getUIElement(UIElementName.STAR_RATING_FILTER).get() instanceof StarRatingFilter : "Expected STAR_RATING_FILTER element to be an instance of StarRatingFilter, but got: " + generalManager.getUIElement(UIElementName.STAR_RATING_FILTER).get().getClass();
    }
    
}
