package org.troy.capstone.data_structures.ItemTable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SieveOfEratosthenesTest {

    @BeforeAll
    public static void setup() {
        SieveOfEratosthenes s = new SieveOfEratosthenes(Short.MAX_VALUE * 2);
    }
    
    @Test
    @DisplayName("Test various numbers ")
    public void testVariousNumbers() {
        SieveOfEratosthenes s = new SieveOfEratosthenes(Short.MAX_VALUE * 2);
        assert s.isPrime(32771) : "32771 should be prime";
        assert !s.isPrime(3432) : "3432 should not be prime";
        assert !s.isPrime(12) : "12 should not be prime";
        assert s.isPrime(67) : "67 should be prime";
    }

    @Test
    @DisplayName("Test prime beyond short range")
    public void testPrimeBeyondShortRangeValue() {
        SieveOfEratosthenes s = new SieveOfEratosthenes(Short.MAX_VALUE * 2);
        int primeBeyondShort = s.primeBeyondShortRange().orElse(-1);
        assertEquals(65521, primeBeyondShort, "Prime beyond short range should be 65521");
        assert s.isPrime(primeBeyondShort) : "Returned value should be prime";
    }
}
