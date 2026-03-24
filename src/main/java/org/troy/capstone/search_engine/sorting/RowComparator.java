package org.troy.capstone.search_engine.sorting;

import java.util.Comparator;
import org.troy.capstone.constants.TableColumnName;
import tech.tablesaw.api.Row;

/**
 * Comparator for sorting Rows by a specified type.
 */
public class RowComparator implements Comparator<Row> {

    /** The type of sorting to be applied by this comparator. */
    private final SortType sortType;

    /** 
     * Enum representing different sorting types for rows.
     */
    public enum SortType {
        PRICE_ASCENDING,
        PRICE_DESCENDING,
        RELEVANCE_ASCENDING,
        RELEVANCE_DESCENDING,
        RATING_ASCENDING,
        RATING_DESCENDING;
    }

    /**
     * Gets an array of RowComparators for all defined SortTypes.
     * @return An array of RowComparators, one for each SortType.
     */
    public static RowComparator[] getComparators() {
        RowComparator[] comparators = new RowComparator[SortType.values().length];
        for (int i = 0; i < SortType.values().length; i++)
            comparators[i] = new RowComparator(SortType.values()[i]);
        return comparators;
    }

    /**
     * Returns a string representation of this RowComparator, which is the name of the SortType with spaces instead of underscores and proper capitalization.
     * @return A string representation of this RowComparator.
     */
    @Override
    public String toString() {
        String[] parts = sortType.name().split("_");
        for (int i = 0; i < parts.length; i++)
            parts[i] = Character.toUpperCase(parts[i].charAt(0)) + parts[i].substring(1).toLowerCase();
        return String.join(" ", parts);
    }

    /**
     * Constructs a RowComparator with the specified SortType.
     * @param sortType The SortType that defines the sorting behavior of this RowComparator.
     */
    public RowComparator(SortType sortType) {
        this.sortType = sortType;
    }

    /**
     * Constructs a RowComparator with the specified SortType name.
     * @param sortTypeName The name of the SortType that defines the sorting behavior of this RowComparator.
     */
    public RowComparator(String sortTypeName) {
        this.sortType = SortType.valueOf(sortTypeName.toUpperCase().replace(" ", "_"));
    }

    /**
     * Compares two Rows based on the SortType of this RowComparator.
     * @param r1 The first Row to be compared.
     * @param r2 The second Row to be compared.
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(Row r1, Row r2) {
        switch (sortType) {
            case PRICE_ASCENDING:
                return Float.compare(r1.getFloat(TableColumnName.PRICE.getColumnName()), r2.getFloat(TableColumnName.PRICE.getColumnName()));
            case PRICE_DESCENDING:
                return Float.compare(r2.getFloat(TableColumnName.PRICE.getColumnName()), r1.getFloat(TableColumnName.PRICE.getColumnName()));
            case RELEVANCE_ASCENDING:
                return Float.compare(r1.getFloat(TableColumnName.RELEVANCE.getColumnName()), r2.getFloat(TableColumnName.RELEVANCE.getColumnName()));
            case RELEVANCE_DESCENDING:
                return Float.compare(r2.getFloat(TableColumnName.RELEVANCE.getColumnName()), r1.getFloat(TableColumnName.RELEVANCE.getColumnName()));
            case RATING_ASCENDING:
                return Float.compare(r1.getFloat(TableColumnName.REVIEW_SCORE.getColumnName()), r2.getFloat(TableColumnName.REVIEW_SCORE.getColumnName()));
            case RATING_DESCENDING:
                return Float.compare(r2.getFloat(TableColumnName.REVIEW_SCORE.getColumnName()), r1.getFloat(TableColumnName.REVIEW_SCORE.getColumnName()));
            default:
                return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RowComparator other = (RowComparator) obj;
        return sortType == other.sortType;
    }

}