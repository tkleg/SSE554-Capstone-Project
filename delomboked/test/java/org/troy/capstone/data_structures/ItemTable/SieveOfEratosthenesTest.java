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

    @Test
    @DisplayName("Test bad length for maxPrimeUnder100mil")
    public void testBadLengthForMaxPrimeUnder100Mil() {
        SieveOfEratosthenes smallSieve = new SieveOfEratosthenes(10);
        assert smallSieve.maxPrimeUnder100mil().isEmpty() : "Should return empty for sieve that doesn't cover up to 100 million";
    }

    @Test
    @DisplayName("Force bottom return statement of maxPrimeUnder100mil for code coverage")
    public void testForceBottomReturnStatement() {
        SieveOfEratosthenes smallSieve = new SieveOfEratosthenes(100_000_000){
            @Override
            public boolean isPrime(int value) {
                //Override isPrime to make the sieve think there are no primes, to force the bottom return statement of maxPrimeUnder100mil
                return false;
            }
        };
        assert smallSieve.maxPrimeUnder100mil().isEmpty() : "Should return empty for sieve that doesn't cover up to 100 million";
    }
}
