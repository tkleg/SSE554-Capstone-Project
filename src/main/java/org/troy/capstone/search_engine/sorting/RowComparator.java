package org.troy.capstone.search_engine.sorting;

import java.util.Comparator;

import org.troy.capstone.constants.TableColumnName;

import tech.tablesaw.api.Row;

/**
 * Comparator for sorting {@code Row}s by a specified type.
 */
public class RowComparator implements Comparator<Row> {

    /** The type of sorting to be applied by this comparator. */
    private final SortType sortType;

    /** 
     * Enum representing different sorting types for rows.
     */
    public enum SortType {
        /** Sort by price in ascending order. */
        PRICE_ASCENDING,
        /** Sort by price in descending order. */
        PRICE_DESCENDING,
        /** Sort by relevance in ascending order. */
        RELEVANCE_ASCENDING,
        /** Sort by relevance in descending order. */
        RELEVANCE_DESCENDING,
        /** Sort by rating in ascending order. */
        RATING_ASCENDING,
        /** Sort by rating in descending order. */
        RATING_DESCENDING;
    }

    /**
     * Gets an array of {@code RowComparator}s for all defined {@code SortType}s.
     * @return An array of {@code RowComparator}s, one for each {@code SortType}.
     */
    public static RowComparator[] getComparators() {
        RowComparator[] comparators = new RowComparator[SortType.values().length];
        for (int i = 0; i < SortType.values().length; i++)
            comparators[i] = new RowComparator(SortType.values()[i]);
        return comparators;
    }

    /**
     * Returns a string representation of this {@code RowComparator}, which is the name of the {@code SortType} with spaces instead of underscores and proper capitalization.
     * @return A string representation of this {@code RowComparator}.
     */
    @Override
    public String toString() {
        String[] parts = sortType.name().split("_");
        for (int i = 0; i < parts.length; i++)
            parts[i] = Character.toUpperCase(parts[i].charAt(0)) + parts[i].substring(1).toLowerCase();
        return String.join(" ", parts);
    }

    /**
     * Constructs a {@code RowComparator} with the specified {@code SortType}.
     * @param sortType The {@code SortType} that defines the sorting behavior of this {@code RowComparator}.
     */
    public RowComparator(SortType sortType) {
        this.sortType = sortType;
    }

    /**
     * Constructs a {@code RowComparator} with the specified {@code SortType} name.
     * @param sortTypeName The name of the {@code SortType} that defines the sorting behavior of this {@code RowComparator}.
     */
    public RowComparator(String sortTypeName) {
        this.sortType = SortType.valueOf(sortTypeName.toUpperCase().replace(" ", "_"));
    }

    /**
     * Returns the {@code SortType} of this {@code RowComparator}.
     * @return The {@code SortType} of this {@code RowComparator}.
     */
    public SortType getSortType() {
        return sortType;
    }

    /**
     * Compares two {@code Row}s based on the {@code SortType} of this {@code RowComparator}.
     * @param r1 The first {@code Row} to be compared.
     * @param r2 The second {@code Row} to be compared.
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(Row r1, Row r2){
        switch (sortType) {
            case PRICE_ASCENDING -> {
                return Float.compare(r1.getFloat(TableColumnName.PRICE.getColumnName()), r2.getFloat(TableColumnName.PRICE.getColumnName()));
            }case PRICE_DESCENDING -> {
                return Float.compare(r2.getFloat(TableColumnName.PRICE.getColumnName()), r1.getFloat(TableColumnName.PRICE.getColumnName()));
            }case RELEVANCE_ASCENDING -> {
                return Float.compare(r1.getFloat(TableColumnName.RELEVANCE.getColumnName()), r2.getFloat(TableColumnName.RELEVANCE.getColumnName()));
            }case RELEVANCE_DESCENDING -> {
                return Float.compare(r2.getFloat(TableColumnName.RELEVANCE.getColumnName()), r1.getFloat(TableColumnName.RELEVANCE.getColumnName()));
            }case RATING_ASCENDING -> {
                return Float.compare(r1.getFloat(TableColumnName.REVIEW_SCORE.getColumnName()), r2.getFloat(TableColumnName.REVIEW_SCORE.getColumnName()));
            }case RATING_DESCENDING -> {
                return Float.compare(r2.getFloat(TableColumnName.REVIEW_SCORE.getColumnName()), r1.getFloat(TableColumnName.REVIEW_SCORE.getColumnName()));
            }case null -> {
                System.out.println("Invalid SortType for RowComparator: " + sortType);
                return 0;
            }
        }
    }
    
}