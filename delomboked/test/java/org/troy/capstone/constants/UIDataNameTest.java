package org.troy.capstone.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class UIDataNameTest {

    @ParameterizedTest
    @EnumSource(UIDataName.class)
    @DisplayName("Test that all UIDataName enum values are correct")
    void testUIDataNameEnum(UIDataName uiDataName) {
        UIDataName expected = UIDataName.valueOf(uiDataName.name());
        assert expected == uiDataName : "UIDataName enum value should match the expected value from valueOf";
        assert uiDataName.getValue().equals( uiDataName.name() ) : "UIDataName enum value should match its name";
        assert uiDataName.getValue().equals( uiDataName.toString() ) : "UIDataName enum value should match its toString()";
    }

}
