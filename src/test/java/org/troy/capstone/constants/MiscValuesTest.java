package org.troy.capstone.constants;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

public class MiscValuesTest {

    @ParameterizedTest
    @EnumSource(MiscValues.class)
    public void testGetValue(MiscValues miscValue) {
        assertNotNull(miscValue.getValue(), "Value should not be null");
    }

    @ParameterizedTest
    @CsvSource({
        "SORTING_THRESHOLD, 25",
        "RECENTLY_VIEWED_QUEUE_SIZE, 10"
    })
    public void testGetValueWithExpected(MiscValues miscValue, int expected) {
        assertNotNull(miscValue.getValue(), "Value should not be null");
        assert miscValue.getValue() == expected : "Expected value for " + miscValue.name() + " is " + expected;
    }
    
}
