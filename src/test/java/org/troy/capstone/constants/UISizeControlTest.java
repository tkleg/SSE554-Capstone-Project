package org.troy.capstone.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class UISizeControlTest {

    @ParameterizedTest
    @EnumSource(UISizeControl.class)
    @DisplayName("Test that all UISizeControl values are greater than 0")
    public void testUISizeControlValues(UISizeControl sizeControl) {
        assert sizeControl.getValue() > 0 : "Size value should be greater than 0 for: " + sizeControl.name();
    }
}
