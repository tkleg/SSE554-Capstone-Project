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
        assert IdHashKey.getPrime() != 0 : "Prime should be set";
        
        BigInteger I = IdHashKey.getI();
        BigInteger J = IdHashKey.getJ();
        int PRIME = IdHashKey.getPrime();

        assert I.compareTo(BigInteger.ONE) >= 0 && I.compareTo(BigInteger.valueOf(PRIME)) < 0 : "I should be in the range [1, PRIME)";
        assert J.compareTo(BigInteger.ZERO) >= 0 && J.compareTo(BigInteger.valueOf(PRIME)) < 0 : "J should be in the range [0, PRIME)";
        assertEquals( 99999989, IdHashKey.getPrime(), "Prime should be 99999989, the largest prime under 100 million" );
    }

}
