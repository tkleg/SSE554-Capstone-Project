package org.troy.capstone.data_structures.ItemTable;
import java.math.BigInteger;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;

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

}
