package org.troy.capstone.constants;

/**
 * The MiscValues enum is a collection of miscellaneous constant values used throughout the application.
 */
public enum MiscValues {
    /** The threshold for deciding when to use insertion sort vs quicksort in the Sorter class. If sorting a size lower or equal to this value, insertion sort will be used, otherwise quicksort will be used. */
    SORTING_THRESHOLD(25),
    /** The maximum number of recently viewed items to keep track of in the RecentlyViewedQueue and display in the RecentlyViewedWindow. */
    RECENTLY_VIEWED_QUEUE_SIZE(10);

    /** The integer value associated with the constant. */
    private final int value;

    /** Constructor for the MiscValues enum.
     * @param value The integer value to associate with the constant.
     */
    MiscValues(int value) {
        this.value = value;
    }

    /** Retrieves the integer value associated with the constant.
     * @return The integer value of the constant.
     */
    public int getValue() {
        return value;
    }
}
