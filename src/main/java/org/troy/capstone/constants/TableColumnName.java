package org.troy.capstone.constants;

import java.util.Set;

/**
 * Enum for column names in the dataset, to avoid hardcoding strings throughout the codebase and reduce risk of typos.
 */
public enum TableColumnName {
    /** Column name for the index of the item. */
    INDEX("index"),
    /** Column name for the ID of the item. */
    ID("id"),
    /** Column name for the URL of the item's image. */
    IMAGE_URL("imageUrl"),
    /** Column name for the name of the item. */
    NAME("name"),
    /** Column name for the publisher of the item. */
    PUBLISHER("publisher"),
    /** Column name for the description of the item. */
    DESCRIPTION("description"),
    /** Column name for the category of the item. */
    CATEGORY("category"),
    /** Column name for the tags associated with the item. */
    TAGS("tags"),
    /** Column name for the price of the item. */
    PRICE("price"),
    /** Column name for the review score of the item. */
    REVIEW_SCORE("reviewScore"),
    /** Column name for the review count of the item. */
    REVIEW_COUNT("reviewCount"),
    /** Column name for the stock quantity of the item. */
    STOCK_QUANTITY("stockQuantity"),
    /** Column name for the date the item was added. */
    DATE_ADDED("dateAdded"),
    /** Column name for the author of the item's photo. */
    PHOTO_AUTHOR("photoAuthor"),
    /** Column name for the URL of the page for the author of the item's photo. */
    PHOTO_AUTHOR_URL("photoAuthorUrl"),
    /** Column name for the relevance score of the item. */
    RELEVANCE("relevance");


    /** The column name represented by this enum constant. */
    private final String columnName;

    /** Set of column names that are considered categorical variables. */
    private static final Set<TableColumnName> categoricalColumns = Set.of(TableColumnName.PUBLISHER, TableColumnName.CATEGORY, TableColumnName.TAGS);

    /** Getter for the set of categorical column names.
     * @return A set of TableColumnName enums that are considered categorical variables.
    */
    public static Set<TableColumnName> getCategoricalColumns() {
        return categoricalColumns;
    }

    /** Constructor for the enum constants.
     * @param columnName The string representation of the column name associated with this enum constant.
    */
    TableColumnName(String columnName) {
        this.columnName = columnName;
    }

    /** Getter for the column name associated with this enum constant.
    * @return The column name as a string.
    */
    public String getColumnName() {
        return columnName;
    }

}
