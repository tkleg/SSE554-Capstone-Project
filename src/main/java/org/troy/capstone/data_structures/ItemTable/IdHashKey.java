package org.troy.capstone.data_structures.ItemTable;

import java.math.BigInteger;

public class IdHashKey {
    private final String value;
    // A prime number larger than the maximum possible hash value from collapsing the strings, to ensure good distribution in universal hashing
    private static final BigInteger P = BigInteger.valueOf(SieveOfEratosthenes.staticPrimeUnder100mil().orElseThrow());
    private static BigInteger I = BigInteger.
        valueOf( (long) (Math.random() * P.longValue()) + 1 );
    private static BigInteger J = BigInteger.
        valueOf( (long) (Math.random() * (P.longValue() - 1L)) );

    //1000 entries, 0.75 load factor -> 2048 table size
    //2048 is the lowest power of 2 above 1000/0.75, a
    private static final int TABLE_SIZE = 2048;

    public IdHashKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    static BigInteger getI() {
        return I;
    }
    
    static BigInteger getJ() {
        return J;
    }
    
    static BigInteger getP() {
        return P;
    }

    static void setI(BigInteger newI) {
        I = newI;
    }

    static void setJ(BigInteger newJ) {
        J = newJ;
    }
    
    //Using universal hashing code from textbook, with small alterations
    @Override
    public int hashCode() {
        BigInteger collapsed = collapseStringToInt(value);
        return I.multiply(collapsed)
            .add(J)
            .mod(BigInteger.valueOf(TABLE_SIZE))
            .intValue();
    }

    /**
     * Algorithm source is https://www.tutorialspoint.com/data_structures_algorithms/rabin_karp_algorithm.htm
     * Rabin-Karp style string to int collapse, using polynomial rolling hash method
     * 
     * pre-conditions: str is not null and only contains ASCII characters from '!' to '~' (printable characters excluding space and delete)
     *
     * @param str (String): The string to collapse into an integer hash value
     * @return hash (BigInteger): A BigInteger hash value representing the input string collapsed via Rabin-Karp rolling hash
     */
    private BigInteger collapseStringToInt(String str) {
        //Number of possible characters (ASCII chars from '!' to '~', printables excluding space and delete)
        BigInteger b = BigInteger.valueOf('~' - '!' + 1);
        int L = str.length();
        BigInteger hash = BigInteger.ZERO;
        for (int i = 0; i < L; i++){
            //Map '!' to 0, '"' to 1, ..., '~' to 93
            int rankingOfChar = str.charAt(i) - '!';
            hash = hash.add(BigInteger.valueOf(rankingOfChar).multiply(b.pow(L - i - 1)));
        }
        return hash.mod(P);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        IdHashKey other = (IdHashKey) obj;
        return value.equals(other.value);
    }
}
