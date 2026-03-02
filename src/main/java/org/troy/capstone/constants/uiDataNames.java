package org.troy.capstone.constants;

public enum uiDataNames {
    MIN_PRICE("MIN_PRICE"),
    MAX_PRICE("MAX_PRICE"),
    SEARCH_QUERY("SEARCH_QUERY"),
    FILTERS_CONTAINER("FILTERS_CONTAINER"),
    MIN_STAR_RATING("MIN_STAR_RATING");

    private final String value;

    uiDataNames(String value) {
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
