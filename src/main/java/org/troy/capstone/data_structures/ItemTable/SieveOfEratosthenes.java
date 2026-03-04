package org.troy.capstone.data_structures.ItemTable;

import java.util.BitSet;
import java.util.Optional;

import org.troy.capstone.annotations.TestExclusionGenerated;

//Code originally sourced from the MindTap assignment, but modified
public class SieveOfEratosthenes {

    BitSet notAPrime;

    public SieveOfEratosthenes(int maxValue) {
        notAPrime = new BitSet(maxValue + 1);

        // 0 and 1 are not prime numbers
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

    public boolean isPrime(int value) {
        return !notAPrime.get(value);
    }

    /*
     * Releases the internal array to be eligible for garbage collection.
     * After calling this, isPrime() will throw NullPointerException.
     */
    public void releaseMemory() {
        notAPrime = null;
    }

    /**
     * Gets the largest prime number under 100 million using the Sieve of Eratosthenes algorithm.
     * 
     * pre-conditions: none
     * 
     * @return prime (Optional<Integer>): an Optional containing the largest prime number under 100 million, or empty if there was an error during calculation.
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
     * pre-conditions: none
     * 
     * @return prime (Optional<Integer>): an Optional containing the largest prime number under 100 million, or empty if there was an error during calculation.
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

    @TestExclusionGenerated
    public static void main(String[] args) {
        SieveOfEratosthenes s = new SieveOfEratosthenes(100_000_000);
        Optional<Integer> primeUnder100mil = s.maxPrimeUnder100mil();
        if( primeUnder100mil.isPresent() )
            System.out.println("Largest prime under 100 million: " + primeUnder100mil.get());
        else
            System.out.println("No prime found under 100 million.");
    }

}
