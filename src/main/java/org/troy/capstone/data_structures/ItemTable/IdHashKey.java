package org.troy.capstone.data_structures.ItemTable;

import java.math.BigInteger;

public class IdHashKey {
    private final String value;
    private static final int PRIME = SieveOfEratosthenes.staticPrimeUnder100mil().get(); // A prime number larger than the maximum possible hash value from collapsing the strings, to ensure good distribution in universal hashing
    private static BigInteger I = BigInteger.
        valueOf( (long) (Math.random() * PRIME) + 1 );
    private static BigInteger J = BigInteger.
        valueOf( (long) (Math.random() * (PRIME - 1L)) );

    // 1000 entries, 0.75 load factor -> 2048 table size
    private static final int TABLE_SIZE = 2048;

    public IdHashKey(String value) {
        this.value = value;
    }
    
    static void reRollHashParameters() {
        I = BigInteger.valueOf( (long) (Math.random() * PRIME) + 1 );
        J = BigInteger.valueOf( (long) (Math.random() * (PRIME - 1L)) );
    } 

    public String getValue() {
        return value;
    }

    static int getPrime() {
        return PRIME;
    }
    
    static BigInteger getI() {
        return I;
    }
    
    static BigInteger getJ() {
        return J;
    }
    
    //Using universal hashing from textbook
    //Adding a Rabin-Karp style string to int collapse to make strings ints
    @Override
    public int hashCode() {
        BigInteger collapsed = collapseStringToInt(value);
        return I.multiply(collapsed)
            .add(J)
            .mod(BigInteger.valueOf(TABLE_SIZE))
            .intValue();
    }

    //Algorithm source is https://www.tutorialspoint.com/data_structures_algorithms/rabin_karp_algorithm.htm
    //Rabin-Karp style string to int collapse, using polynomial rolling hash method
    private BigInteger collapseStringToInt(String str) {
        BigInteger b = BigInteger.valueOf('~' - '!' + 1); // Number of possible characters (ASCII range from '!' to '~', printables with no space)
        int L = str.length();
        BigInteger hash = BigInteger.ZERO;
        for (int i = 0; i < L; i++){
            int rankingOfChar = str.charAt(i) - '!' + 1; // Map '!' to 1, '"' to 2, ..., '~' to 94
            hash = hash.add(BigInteger.valueOf(rankingOfChar).multiply(b.pow(L - i - 1)));
        }
        return hash.mod(BigInteger.valueOf(PRIME)); // Mod by a prime to keep the hash value manageable
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        IdHashKey other = (IdHashKey) obj;
        return value != null ? value.equals(other.value) : other.value == null;
    }
}
