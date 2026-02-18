package org.troy.capstone.data_structures.ItemTable;

import java.util.Optional;

import org.troy.capstone.annotations.TestExclusionGenerated;

//Code originally sourced from the MindTap assignment, but modified to find the largest prime smaller or equal to (2 * Short.MAX_VALUE) instead of the smallest prime greater than Short.MAX_VALUE
public class SieveOfEratosthenes {

    boolean[] notAPrime;

    public SieveOfEratosthenes(int maxValue) {
        notAPrime = new boolean[maxValue + 1];

        // 0 and 1 are not prime numbers
        notAPrime[0] = true;
        notAPrime[1] = true;
        
        int p = 2;
        boolean newP = true;
        while(newP){
            newP = false;
            for( int i = p * 2; i <= maxValue; i += p )
                notAPrime[i] = true;
            for( int i = p+1; i <= maxValue; i++ )
                if( !notAPrime[i] ){
                    p = i;
                    newP = true;
                    break;
                }
        }
    }

    public boolean isPrime(int value) {
        return !notAPrime[value];
    }

    public static Optional<Integer> staticPrimeUnder1mil() {
        SieveOfEratosthenes s = new SieveOfEratosthenes(1_000_000);
        return s.maxPrimeUnder1mil();
        }

    public Optional<Integer> maxPrimeUnder1mil(){
        if( notAPrime.length < 1_000_000 )
            return Optional.empty();
        for( int i = notAPrime.length - 1; i >= 0; i-- )
            if( isPrime(i) )
                return Optional.of(i);
        return Optional.empty();
    }

    @TestExclusionGenerated
    public static void main(String[] args) {
        System.out.println("Short.MAX_VALUE: " + Short.MAX_VALUE);
        SieveOfEratosthenes s = new SieveOfEratosthenes(1_000_000);
        Optional<Integer> primeUnder1mil = s.maxPrimeUnder1mil();
        if( primeUnder1mil.isPresent() )
            System.out.println("Largest prime under 1 million: " + primeUnder1mil.get());
        else
            System.out.println("No prime found under 1 million.");
    }

}
