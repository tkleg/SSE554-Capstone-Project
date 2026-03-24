package org.troy.capstone.search_engine.sorting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LongWrapperTest {

    @Test
    @DisplayName("Test LongWrapper basic functionality")
    public void testLongWrapper() {
        LongWrapper longWrapper = new LongWrapper();
        assert longWrapper.getValue() == 0 : "Default constructor should initialize value to 0";

        longWrapper.setValue(12345L);
        assert longWrapper.getValue() == 12345L : "setValue should update the value correctly";

        LongWrapper longWrapperWithValue = new LongWrapper(67890L);
        assert longWrapperWithValue.getValue() == 67890L : "Constructor with value should initialize the value correctly";
        
        String stringValue = longWrapper.toString();
        assert stringValue.equals("12345") : "toString should return the string representation of the value";
    }

}
