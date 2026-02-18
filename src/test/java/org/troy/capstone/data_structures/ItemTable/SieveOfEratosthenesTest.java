package org.troy.capstone.data_structures.ItemTable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SieveOfEratosthenesTest {
    private static SieveOfEratosthenes s;

    @BeforeAll
    public static void setup() {
        s = new SieveOfEratosthenes(100_000_000);
    }
    
    @Test
    @DisplayName("Test various numbers ")
    public void testVariousNumbers() {
        assert s.isPrime(32771) : "32771 should be prime";
        assert !s.isPrime(3432) : "3432 should not be prime";
        assert !s.isPrime(12) : "12 should not be prime";
        assert s.isPrime(67) : "67 should be prime";
    }

    @Test
    @DisplayName("Test prime under 100 million")
    public void testPrimeUnder100Million() {
        int primeUnder100Million = s.maxPrimeUnder100mil().orElse(-1);
        assertEquals(99999989, primeUnder100Million, "Prime under 100 million should be 99999989");
        assert s.isPrime(primeUnder100Million) : "Returned value should be prime";
    }
}
