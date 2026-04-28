package org.troy.capstone.constants;

/** Enum representing the IDs of various UI components for {@code TestFX} testing. */
public enum TestFXId {
    /** ID for the sort option dropdown in the UI */
    SORT_OPTION_DROPDOWN("sortOptionDropdown"),

    /** ID prefix for cells in the sort option dropdown, where the full ID includes {@code RowComparator.toString()} after the prefix */
    SORT_OPTION_CELL_PREFIX("sortOptionCell_"),

    /** ID for the search field in the UI */
    SEARCH_FIELD("searchField"),

    /** ID for the min price slider in the UI */
    MIN_PRICE_SLIDER("minPriceSlider"),

    /** ID for the max price slider in the UI */
    MAX_PRICE_SLIDER("maxPriceSlider"),

    /** ID prefix for star rating labels in the UI, where the full ID just includes the rating */
    STAR_LABEL_PREFIX("starLabel_"),

    /** ID for the star rating filter component in the UI */
    STAR_RATING_FILTER("starRatingFilter"),

    /** ID prefix for filter checkboxes in the UI, where the full ID includes the filter option */
    CHECKBOX_PREFIX("checkbox_"),

    /** ID prefix for filter panels in the UI, where the full ID includes the filter category */
    FILTER_PANEL_PREFIX("filterPanel_"),

    /** ID for the name label of the first searchedItemPanel. Used for testing to allow retrieval of the first searched item's name label */
    FIRST_SEARCHED_ITEM_NAME_LABEL("searchedItemNameLabel_1");

    /** The string ID associated with the {@code TestFXId} enum value. */
    private final String id;

    /** Constructs a {@code TestFXId} enum value with the specified string ID.
     * @param id The string ID to be associated with the {@code TestFXId} enum value.
     */
    TestFXId(String id) {
        this.id = id;
    }

    /** Returns the string ID associated with this {@code TestFXId} enum value.
     * @return The string ID associated with this {@code TestFXId} enum value.
     */
    public String getId() {
        return id;
    }
}