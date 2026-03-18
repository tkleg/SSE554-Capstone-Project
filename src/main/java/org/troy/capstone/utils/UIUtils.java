package org.troy.capstone.utils;

import org.troy.capstone.annotations.TestExclusionGenerated;

import javafx.scene.layout.Region;

/**
 * Utility class for UI-related functions.
 */
public class UIUtils {

    //Never called, just prevents Jacoco from complaining about missing code coverage for the default constructor
    @TestExclusionGenerated
    private UIUtils() {
    }

    /**
     * Sets the size of a JavaFX Region (such as a Pane, Button, etc.) to the specified width and height. If either width or height is null, that dimension will not be modified.
     * 
     * @pre <ul><li>The region parameter should be a valid JavaFX Region object.</li></ul>
     * 
     * @param region The JavaFX Region to set the size of.
     * @param width The desired width to set for the region, or null to leave the width unchanged.
     * @param height The desired height to set for the region, or null to leave the height unchanged.
     */
    public static void setSize(Region region, Integer width, Integer height) {
        if (width != null) {
            region.setMaxWidth(width);
            region.setMinWidth(width);
        }
        if (height != null) {
            region.setMaxHeight(height);
            region.setMinHeight(height);
        }
    }
}
