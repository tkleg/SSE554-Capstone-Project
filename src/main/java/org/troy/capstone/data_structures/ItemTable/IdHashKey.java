package org.troy.capstone.data_structures.ItemTable;

import java.math.BigInteger;

public class IdHashKey {
    private final String value;
    private static final int PRIME = SieveOfEratosthenes.staticPrimeBeyondShortRange().get();
    private static final BigInteger i = BigInteger.
        valueOf( (long) (Math.random() * PRIME) + 1 );
    private static final BigInteger j = BigInteger.
        valueOf( (long) (Math.random() * (PRIME - 1L)) );

    // 1000 entries, 0.75 load factor -> 2048 table size
    private static final int TABLE_SIZE = 2048;

    public IdHashKey(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    //Using universal hashing from textbook
    //Adding a Rabin-Karp style string to int collapse to make strings ints
    @Override
    public int hashCode() {
        int collapsed = collapseStringToInt(value);
        return i.multiply(BigInteger.valueOf(collapsed))
            .add(j)
            .mod(BigInteger.valueOf(TABLE_SIZE))
            .intValue();
    }

    //Algorithm source is https://www.tutorialspoint.com/data_structures_algorithms/rabin_karp_algorithm.htm
    private int collapseStringToInt(String str) {
        int b = str.length();
        int hash = 0;
        for (int i = 0; i < str.length(); i++)
            hash = (hash * (int) Math.pow(b, b - i - 1) + str.charAt(i)) % PRIME;
        return hash;
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
