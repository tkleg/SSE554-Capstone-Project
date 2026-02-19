package org.troy.capstone.constants;

public enum tableColumns {
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

    tableColumns(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
