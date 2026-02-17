package org.troy.capstone.data_structures.ItemTable;

public class ShortHashKey {
    private final short value;
    
    public ShortHashKey(short value) {
        this.value = value;
    }
    
    public short getValue() {
        return value;
    }
    
    @Override
    public int hashCode() {
        return Math.abs(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return value == ( (ShortHashKey) obj ).value;
    }
}
