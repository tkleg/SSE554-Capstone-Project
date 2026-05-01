package org.troy.capstone.data_structures.item_table;

import java.math.BigInteger;

/**
 * Class representing a hash key for item IDs, using universal hashing to minimize collisions in the item table. The hash code is computed by collapsing the string ID into an integer using a {@code Rabin-Karp style polynomial rolling hash method}, and then applying a universal hash function with random coefficients {@code I} and {@code J} and a prime modulus {@code P}. This approach helps ensure a good distribution of hash values even for similar string IDs, which is important for maintaining efficient lookups in the item table. Implements {@code Cloneable} to allow for deep copy of {@code IdHashKey} instances.
 */
public class IdHashKey implements Cloneable {
    /** The original string value of the item ID, stored for equality checks and potential debugging purposes. */
    private final String value;

    /** A prime number larger than the maximum possible hash value from collapsing the strings, to ensure good distribution in universal hashing. */
    private static final BigInteger P = BigInteger.valueOf(SieveOfEratosthenes.staticPrimeUnder100mil().orElseThrow());
    /**
     * A random coefficient for the universal hash function, chosen uniformly from 1 to P-1.
     * Used as the multiplier in the universal hash formula. Not final to allow changing in testing.
     */
    @SuppressWarnings("FieldMayBeFinal")
    private static BigInteger I = BigInteger.valueOf((long) (Math.random() * P.longValue()) + 1);

    /**
     * A random coefficient for the universal hash function, chosen uniformly from 0 to P-1.
     * Used as the additive constant in the universal hash formula. Not final to allow changing in testing.
     */
    @SuppressWarnings("FieldMayBeFinal")
    private static BigInteger J = BigInteger.valueOf((long) (Math.random() * (P.longValue() - 1L)));

    /** 961 entries, 0.75 load factor -> 2048 table size
    2048 is the lowest power of 2 above 961/0.75.
    The table size is chosen to be a power of 2 for efficient modulo operations. */
    private static final int TABLE_SIZE = 2048;

    /** Constructor for the IdHashKey class.
     * @param value The original string value of the item ID to be hashed.
    */
    public IdHashKey(String value) {
        this.value = value;
    }

    /** Getter for the original string value of the item ID.
     * @return The original string value of the item ID.
    */
    public String getValue() {
        return value;
    }
    
    /** Getter for the random coefficient I used in the universal hash function.
     * @return The random coefficient I.
    */
    static BigInteger getI() {
        return I;
    }
    
    /** Getter for the random coefficient J used in the universal hash function.
     * @return The random coefficient J.
    */
    static BigInteger getJ() {
        return J;
    }
    
    /** Getter for the prime modulus P used in the universal hash function.
     * @return The prime modulus P.
    */
    static BigInteger getP() {
        return P;
    }
    
    /** Using universal hashing code from textbook, with small alterations.
     * @return The hash code for the item ID.
     */
    @Override
    public int hashCode() {
        BigInteger collapsed = collapseStringToInt(value);
        return I.multiply(collapsed)
            .add(J)
            .mod(BigInteger.valueOf(TABLE_SIZE))
            .intValue();
    }

    /**
     * Rabin-Karp style string to int collapse, using polynomial rolling hash method.
     * Algorithm source is https://www.tutorialspoint.com/data_structures_algorithms/rabin_karp_algorithm.htm.
     * @pre str is not null and only contains ASCII characters from '!' to '~' (printable characters excluding space and delete).
     * 
     * @param str The string to collapse into an integer hash value.
     * @return A hash value representing the input string collapsed via Rabin-Karp rolling hash.
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

    /**
     * Checks if this {@code IdHashKey} is equal to another object.
     * @param obj The object to compare with.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        IdHashKey other = (IdHashKey) obj;
        return value.equals(other.value);
    }

    /** Returns a string representation of the {@code IdHashKey}, including the original string value and the computed hash code.
     * @return A string representation of the {@code IdHashKey}.
    */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
