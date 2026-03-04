package org.troy.capstone.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class UIElementNameTest {

    @ParameterizedTest
    @EnumSource(UIElementName.class)
    @DisplayName("Test that all UIElementName enum values are correct")
    void testUIElementNameEnum(UIElementName uiElementName) {
        UIElementName expected = UIElementName.valueOf(uiElementName.name());
        assert uiElementName == expected : "UIElementName enum value should match the expected value from valueOf";
        assert uiElementName.getValue().equals( uiElementName.name() ) : "UIElementName enum value should match its name";
        assert uiElementName.getValue().equals( uiElementName.toString() ) : "UIElementName enum value should match its toString()";
    }

}