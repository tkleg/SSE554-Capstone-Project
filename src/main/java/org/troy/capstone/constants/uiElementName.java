package org.troy.capstone.constants;

public enum uiElementName {
    ITEM_SCROLLER("ITEM_SCROLLER"),
    FILTERS_CONTAINER("FILTERS_CONTAINER"),
    MIN_PRICE_SLIDER("MIN_PRICE_SLIDER"),
    MAX_PRICE_SLIDER("MAX_PRICE_SLIDER"),
    SEARCH_FIELD("SEARCH_FIELD"),
    SEARCH_BUTTON("SEARCH_BUTTON");

    private final String value;

    uiElementName(String value) {
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