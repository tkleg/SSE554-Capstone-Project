package org.troy.capstone.constants;

/**
 * The MiscValues enum is a collection of miscellaneous constant values used throughout the application.
 */
public enum MiscValues {
    /** The threshold for deciding when to use insertion sort vs quicksort in the Sorter class. If sorting a size lower or equal to this value, insertion sort will be used, otherwise quicksort will be used. */
    SORTING_THRESHOLD(25),
    /** The maximum number of recently viewed items to keep track of in the RecentlyViewedQueue and display in the RecentlyViewedWindow. */
    RECENTLY_VIEWED_QUEUE_SIZE(10),
    /** The minimum similarity score required for two items to be considered similar in the SimilarItemsGraph. */
    MIN_SIMILARITY_SCORE(3.0811720300000003f),
    /** The number of similar items to display for selected items. */
    NUM_SIMILAR_ITEMS_TO_DISPLAY(10);

    /** The float value associated with the constant. */
    private final float value;

    /** Constructor for the MiscValues enum.
     * @param value The float value to associate with the constant.
     */
    MiscValues(float value) {
        this.value = value;
    }

    /** Retrieves the float value associated with the constant.
     * @return The float value of the constant.
     */
    public float getValue() {
        return value;
    }

    /** Retrieves the integer value associated with the constant. Will floor floats to integers.
     * @return The integer value of the constant.
     */
    public int getIntValue() {
        return (int) value;
    }

}
