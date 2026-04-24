package org.troy.capstone.search_engine.sorting;

/**
 * A simple wrapper class for a long value, this is used for tracking the speed of sorting algorithms. Needed for mutability when passing to a function to allow pass-by-reference.
 */
public class LongWrapper {
    /**
     * The long value being held.
     */
    private long value;

    /**
     * Constructs a {@code LongWrapper} with an initial value of 0.
     */
    public LongWrapper() {}

    /**
     * Constructs a {@code LongWrapper} with the specified initial value.
     * @param value The initial long value to be held by this LongWrapper.
     */
    public LongWrapper(long value) {
        this.value = value;
    }

    /**
     * Returns the long value held by this {@code LongWrapper}.
     * @return The long value held by this {@code LongWrapper}.
     */
    public long getValue() {
        return value;
    }

    /**
     * Sets the long value held by this {@code LongWrapper}.
     * @post The long value held by this {@code LongWrapper} is updated to the specified value.
     * @param value The long value to be held by this {@code LongWrapper}.
     */
    public void setValue(long value) {
        this.value = value;
    }

    /**
     * Returns a string representation of the long value held by this {@code LongWrapper}.
     * @return A string representation of the long value held by this {@code LongWrapper}.
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
