package org.troy.capstone.constants;

/**
 * Enum for UI data names, to avoid hardcoding strings throughout the codebase and reduce risk of typos.
 */
public enum UIDataName {
    /** UI data name for the minimum price filter. */
    MIN_PRICE("MIN_PRICE"),
    /** UI data name for the maximum price filter. */
    MAX_PRICE("MAX_PRICE"),
    /** UI data name for the search query input. */
    SEARCH_QUERY("SEARCH_QUERY"),
    /** UI data name for the selected category filter. */
    FILTERS_CONTAINER("FILTERS_CONTAINER"),
    /** UI data name for the minimum star rating filter. */
    MIN_STAR_RATING("MIN_STAR_RATING"),
    /** UI data name for the sorting option. */
    SORTING_OPTION("SORTING_OPTION");
    
    /** The string value associated with this enum constant, used for UI data keys. */
    private final String value;

    /** Constructor a {@code UIDataName} enum constant with the specified string value.
     * @param value The string value to associate with this enum constant.
    */
    UIDataName(String value) {
        this.value = value;
    }

    /** Getter for the string value associated with this enum constant.
     * @return The string value that can be used as a key in UI data storage.
    */
    public String getValue() {
        return value;
    }

}
