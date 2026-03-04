package org.troy.capstone.data_structures.ItemTable;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

public class IdHashKeyTest {
    
    @Test
    @DisplayName("Test Prime, I, and J parameters are set")
    void testHashParameters(){
        // Just check that I and J are not null and are within the expected range (1 to PRIME-1 for I, 0 to PRIME-1 for J)
        assert IdHashKey.getI() != null : "I should be set";
        assert IdHashKey.getJ() != null : "J should be set";
        assert IdHashKey.getP() != null : "Prime should be set";
        
        BigInteger I = IdHashKey.getI();
        BigInteger J = IdHashKey.getJ();
        BigInteger P = IdHashKey.getP();

        assert I.compareTo(BigInteger.ONE) >= 0 && I.compareTo(P) < 0 : "I should be in the range [1, P)";
        assert J.compareTo(BigInteger.ZERO) >= 0 && J.compareTo(P) < 0 : "J should be in the range [0, P)";
        assertEquals( BigInteger.valueOf(99999989), IdHashKey.getP(), "Prime should be 99999989, the largest prime under 100 million" );
    }

    @Test
    @DisplayName("Test that hash codes are consistent for the same string")
    void testHashCodeConsistency(){
        String testString = "test-item-id";
        IdHashKey key1 = new IdHashKey(testString);
        IdHashKey key2 = new IdHashKey(testString);
        assertEquals( key1.hashCode(), key2.hashCode(), "Hash codes should be consistent for the same string" );
    }

    @Nested
    @DisplayName("Test entire equals method")
    class EqualsMethodTests {

        @Test
        @DisplayName("Test equals with same object")
        void testEqualsWithSameObject() {
            String testString = "test-item-id";
            IdHashKey key = new IdHashKey(testString);
            assert key.equals(key) : "Equals should return true when comparing the same object";
        }

        @Test
        @DisplayName("Test equals with equal objects")
        void testEqualsWithEqualObjects() {
            String testString = "test-item-id";
            IdHashKey key1 = new IdHashKey(testString);
            IdHashKey key2 = new IdHashKey(testString);
            assert key1.equals(key2) : "Equals should return true for equal objects";
        }

        @Test
        @DisplayName("Test equals with different objects")
        void testEqualsWithDifferentObjects() {
            IdHashKey key1 = new IdHashKey("test-item-id-1");
            IdHashKey key2 = new IdHashKey("test-item-id-2");
            assert !key1.equals(key2) : "Equals should return false for different objects";
        }

        @Test
        @DisplayName("Test equals with second variable being null")
        void testEqualsWithNull() {
            IdHashKey key = new IdHashKey("test-item-id");
            assert !key.equals(null) : "Equals should return false when comparing with null";
        }

        @Test
        @DisplayName("Test equals with different class")
        void testEqualsWithDifferentClass() {
            IdHashKey key = new IdHashKey("test-item-id");
            String other = "test-item-id";
            assert !key.equals(other) : "Equals should return false when compared with different class";
        }
    }

}
