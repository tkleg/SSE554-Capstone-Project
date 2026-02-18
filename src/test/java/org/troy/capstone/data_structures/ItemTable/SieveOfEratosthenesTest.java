package org.troy.capstone.data_structures.ItemTable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SieveOfEratosthenesTest {
    private static SieveOfEratosthenes s;

    @BeforeAll
    public static void setup() {
        s = new SieveOfEratosthenes(1_000_000);
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
    @DisplayName("Test prime under 1 million")
    public void testPrimeUnder1Million() {
        int primeUnder1Million = s.maxPrimeUnder1mil().orElse(-1);
        assertEquals(999983, primeUnder1Million, "Prime under 1 million should be 999983");
        assert s.isPrime(primeUnder1Million) : "Returned value should be prime";
    }
}
