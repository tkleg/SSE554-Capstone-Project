package org.troy.capstone.constants;

/**
 * Enum for UI element names, to avoid hardcoding strings throughout the codebase and reduce risk of typos.
 */
public enum UIElementName {
    /** UI element name for the container holding all filters. */
    FILTERS_CONTAINER("FILTERS_CONTAINER"),
    /** UI element name for the container holding the pagination controls for searched items. */
    SEARCHED_ITEM_PAGINATION("SEARCHED_ITEM_PAGINATION"),
    /** UI element name for the minimum price slider filter. */
    MIN_PRICE_SLIDER("MIN_PRICE_SLIDER"),
    /** UI element name for the maximum price slider filter. */
    MAX_PRICE_SLIDER("MAX_PRICE_SLIDER"),
    /** UI element name for the search query input field. */
    SEARCH_FIELD("SEARCH_FIELD"),
    /** UI element name for the star rating filter. */
    STAR_RATING_FILTER("STAR_RATING_FILTER"),
    /** UI element name for the sorting option dropdown. */
    SORTING_OPTION_DROPDOWN("SORTING_OPTION_DROPDOWN"),
    /** UI element name for the recently viewed window. */
    RECENTLY_VIEWED_WINDOW("RECENTLY_VIEWED_WINDOW"),
    /** UI element name for the container holding similar items. */
    SIMILAR_ITEMS_CONTAINER("SIMILAR_ITEMS_CONTAINER");

    /** The string value associated with this enum constant, used for UI element identifiers. */
    private final String value;

    /** Constructor for the enum constants.
     * @param value The string value to associate with this enum constant.
    */
    UIElementName(String value) {
        this.value = value;
    }

    /** Getter for the string value associated with this enum constant.
     * @return The string value that can be used as an identifier for UI elements.
    */
    public String getValue() {
        return value;
    }

}