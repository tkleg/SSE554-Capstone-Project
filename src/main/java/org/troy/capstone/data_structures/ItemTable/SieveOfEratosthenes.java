package org.troy.capstone.data_structures.ItemTable;

import java.util.Optional;

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

    public Optional<Integer> primeBeyondShortRange(){
        if( Short.MAX_VALUE * 2 > notAPrime.length )// If our sieve doesn't cover the range we need, we can't find the prime
            return Optional.empty();

        for( int i = Short.MAX_VALUE * 2; i > Short.MAX_VALUE; i-- )
            if( isPrime(i) )
                return Optional.of(i);
            
        return Optional.empty();
    }

    public static void main(String[] args) {
        System.out.println("Short.MAX_VALUE: " + Short.MAX_VALUE);
        SieveOfEratosthenes s = new SieveOfEratosthenes(Short.MAX_VALUE * 2);
        Optional<Integer> primeBeyondShort = s.primeBeyondShortRange();
        if( primeBeyondShort.isPresent() )
            System.out.println("Largest prime smaller or equal to (2 * Short.MAX_VALUE): " + primeBeyondShort.get());
        else
            System.out.println("No prime found beyond short range.");
    }

}
