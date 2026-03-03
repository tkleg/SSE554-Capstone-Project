package org.troy.capstone.constants;

public enum UIElementName {
    ITEM_SCROLLER("ITEM_SCROLLER"),
    FILTERS_CONTAINER("FILTERS_CONTAINER"),
    SEARCHED_ITEM_PAGINATION("SEARCHED_ITEM_PAGINATION"),
    MIN_PRICE_SLIDER("MIN_PRICE_SLIDER"),
    MAX_PRICE_SLIDER("MAX_PRICE_SLIDER"),
    SEARCH_FIELD("SEARCH_FIELD"),
    SEARCH_BUTTON("SEARCH_BUTTON"),
    STAR_RATING_FILTER("STAR_RATING_FILTER");

    private final String value;

    UIElementName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}