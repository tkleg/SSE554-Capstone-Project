package org.troy.capstone.utils;

import org.troy.capstone.annotations.TestExclusionGenerated;

import javafx.scene.layout.Region;

public class UIUtils {

    //Never called, just prevents Jacoco from complaining about missing code coverage for the default constructor
    @TestExclusionGenerated
    private UIUtils() {
    }

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
