package org.troy.capstone.utils;

import javafx.scene.layout.Region;

public class UIUtils {
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
