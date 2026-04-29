package org.troy.capstone.data_structures.item_table;

import java.util.BitSet;
import java.util.Optional;

/**
 * Code originally sourced from the MindTap assignment, but modified to be more memory efficient by using a BitSet to track which numbers are not prime, and to include a method for getting the largest prime under 100 million.
 * This Class implements the {@code Sieve of Eratosthenes} algorithm to find prime numbers up to a set max value.
 */
public class SieveOfEratosthenes {

    /** A BitSet where the value at index i indicates whether i is not a prime number (true means not prime, false means potentially prime). */
    private BitSet notAPrime;

    /** Constructor for the {@code SieveOfEratosthenes} class, which initializes the sieve up to the specified maximum value.
     * @param maxValue The maximum value up to which to calculate prime numbers. Must be a positive integer greater than or equal to 2.
     */
    public SieveOfEratosthenes(int maxValue) {
        notAPrime = new BitSet(maxValue + 1);

        //0 and 1 are not prime numbers
        notAPrime.set(0);
        notAPrime.set(1);
        
        int p = 2;
        boolean newP = true;
        while(newP){
            newP = false;
            for( int i = p * 2; i <= maxValue; i += p )
                notAPrime.set(i);
            for( int i = p+1; i <= maxValue; i++ )
                if( !notAPrime.get(i) ){
                    p = i;
                    newP = true;
                    break;
                }
        }
    }

    /**
     * Checks if a given value is a prime number.
     * 
     * @param value The value to check for primality. Must be a non-negative integer.
     * @return true if the value is prime, false otherwise.
     */
    public boolean isPrime(int value) {
        return !notAPrime.get(value);
    }

    /**
     * Releases the internal array to be eligible for garbage collection.
     * @post {@code isPrime()} will throw {@code NullPointerException}.
     */
    private void releaseMemory() {
        notAPrime = null;
    }

    /**
     * Gets the largest prime number under 100 million using the Sieve of Eratosthenes algorithm.
     * 
     * @post {@code isPrime()} will throw {@code NullPointerException} due to memory release, as the sieve is no longer needed after finding the largest prime under 100 million.
     * 
     * @return prime An {@code Optional} containing the largest prime number under 100 million, or empty if there was an error during calculation.
     */
    public static Optional<Integer> staticPrimeUnder100mil() {
        SieveOfEratosthenes s = new SieveOfEratosthenes(100_000_000);
        Optional<Integer> result = s.maxPrimeUnder100mil();
        s.releaseMemory();
        return result;
    }
    
    /**
     * Gets the largest prime number under 100 million using the Sieve of Eratosthenes algorithm.
     * 
     * @return prime An {@code Optional} containing the largest prime number under 100 million, or empty if there was an error during calculation.
     */
    public Optional<Integer> maxPrimeUnder100mil(){
        if( notAPrime.length() < 100_000_000 )
            return Optional.empty();
        for( int i = notAPrime.length() - 1; i >= 0; i-- )
            if( isPrime(i) )
                return Optional.of(i);
        //Defensive programming, should never be reached with a properly functioning sieve of sufficient size
        //as there will always be primes in the range [2, 99,999,999]
        return Optional.empty();
    }

}
