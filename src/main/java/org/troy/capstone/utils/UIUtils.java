package org.troy.capstone.utils;

import org.troy.capstone.annotations.Generated;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * Utility class for UI-related functions.
 */
public class UIUtils {

    /**
     * Only exists to prevent Jacoco from complaining about the default constructor not being tested.
     * As the only function of this class is to provide static methods, there is no reason for it to be instantiated, so the constructor is private.
     */
    @Generated
    private UIUtils() {
    }

    /**
     * Sets the size of a JavaFX Region (such as a Pane, Button, etc.) to the specified width and height. If either width or height is null, that dimension will not be modified.
     * 
     * @pre The region parameter should be a valid JavaFX Region object.
     * 
     * @param region The JavaFX Region to set the size of.
     * @param width The desired width to set for the region, or null to leave the width unchanged.
     * @param height The desired height to set for the region, or null to leave the height unchanged.
     */
    public static void setSize(Region region, Integer width, Integer height) {
        if (width != null) {
            region.setMaxWidth(width);
            region.setPrefWidth(width);
            region.setMinWidth(width);
        }
        if (height != null) {
            region.setMaxHeight(height);
            region.setPrefHeight(height);
            region.setMinHeight(height);
        }
    }

    /** Sets a solid black line border on a Region object with a specified corner radius and border width.
     * @pre The region parameter should be a valid JavaFX Region object, and cornerRadii and borderWidth should be non-negative integers.
     * @post The specified Region will have a solid black line border applied to it with the given corner radius and border width.
     * @param region The JavaFX Region to set the border on.
     * @param cornerRadii The radius of the corners of the border, in pixels.
     * @param borderWidth The width of the border, in pixels.
     */
    public static void setLineBorder(Region region, int cornerRadii, int borderWidth) {
        
        region.setBorder(new Border(new BorderStroke(
            Color.BLACK, 
            BorderStrokeStyle.SOLID, 
            new CornerRadii(cornerRadii), 
            new BorderWidths(borderWidth)
        )));

    }

}
