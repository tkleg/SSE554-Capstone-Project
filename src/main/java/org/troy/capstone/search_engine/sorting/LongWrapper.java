package org.troy.capstone.search_engine.sorting;

/**
 * A simple wrapper class for a long value, this is used for tracking the speed of sorting algorithms.
 */
public class LongWrapper {
    /**
     * The long value being held.
     */
    private long value;

    /**
     * Constructs a LongWrapper with an initial value of 0.
     */
    public LongWrapper() {}

    /**
     * Constructs a LongWrapper with the specified initial value.
     * @param value The initial long value to be held by this LongWrapper.
     */
    public LongWrapper(long value) {
        this.value = value;
    }

    /**
     * Returns the long value held by this LongWrapper.
     * @return The long value held by this LongWrapper.
     */
    public long getValue() {
        return value;
    }

    /**
     * Sets the long value held by this LongWrapper.
     * @post The long value held by this LongWrapper is updated to the specified value.
     * @param value The long value to be held by this LongWrapper.
     */
    public void setValue(long value) {
        this.value = value;
    }

    /**
     * Returns a string representation of the long value held by this LongWrapper.
     * @return A string representation of the long value held by this LongWrapper.
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
