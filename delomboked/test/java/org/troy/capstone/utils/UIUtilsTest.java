package org.troy.capstone.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javafx.scene.layout.Region;

public class UIUtilsTest {

    private static Region region;

    @BeforeAll
    public static void setup() {
        region = new Region();
    }

    @ParameterizedTest
    @CsvSource(value = {
        "100, 200",
        "null, 150",
        "250, null",
        "null, null"
    }, nullValues = {"null"})
    @DisplayName("Test UIUtils setSize method with various width and height values")
    public void testSetSize(Integer width, Integer height) {
        UIUtils.setSize(region, width, height);

        if (width != null) {
            assert region.getMaxWidth() == width : "Max width should be set to " + width;
            assert region.getMinWidth() == width : "Min width should be set to " + width;
        }

        if (height != null) {
            assert region.getMaxHeight() == height : "Max height should be set to " + height;
            assert region.getMinHeight() == height : "Min height should be set to " + height;
        }
    }
}
