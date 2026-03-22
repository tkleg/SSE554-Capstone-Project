package org.troy.capstone.search_engine.sorting;

public class LongWrapper {
    private long value;
    public LongWrapper() {}
    public LongWrapper(long value) {
        this.value = value;
    }
    public long getValue() {
        return value;
    }
    public void setValue(long value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
