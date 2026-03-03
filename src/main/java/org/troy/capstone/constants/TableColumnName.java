package org.troy.capstone.constants;

import java.util.Set;

public enum TableColumnName {
    INDEX("index"),
    ID("id"),
    IMAGE_URL("imageUrl"),
    NAME("name"),
    PUBLISHER("publisher"),
    DESCRIPTION("description"),
    CATEGORY("category"),
    TAGS("tags"),
    PRICE("price"),
    REVIEW_SCORE("reviewScore"),
    REVIEW_COUNT("reviewCount"),
    STOCK_QUANTITY("stockQuantity"),
    DATE_ADDED("dateAdded"),
    PHOTO_AUTHOR("photoAuthor"),
    PHOTO_AUTHOR_URL("photoAuthorUrl");

    private final String columnName;

    private static final Set<TableColumnName> categoricalColumns = Set.of(TableColumnName.PUBLISHER, TableColumnName.CATEGORY, TableColumnName.TAGS);

    public static Set<TableColumnName> getCategoricalColumns() {
        return categoricalColumns;
    }

    TableColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

}
