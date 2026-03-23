package org.troy.capstone.search_engine.sorting;

import java.util.Comparator;
import org.troy.capstone.constants.TableColumnName;
import tech.tablesaw.api.Row;

/**
 * Comparator for sorting Rows by a specified type.
 */
public class RowComparator implements Comparator<Row> {
    public enum SortType {
        PRICE_ASCENDING,
        PRICE_DESCENDING,
        RELEVANCE_ASCENDING,
        RELEVANCE_DESCENDING,
        RATING_ASCENDING,
        RATING_DESCENDING;
    }

    public static RowComparator[] getComparators() {
        RowComparator[] comparators = new RowComparator[SortType.values().length];
        for (int i = 0; i < SortType.values().length; i++)
            comparators[i] = new RowComparator(SortType.values()[i]);
        return comparators;
    }

    @Override
    public String toString() {
        String[] parts = sortType.name().split("_");
        for (int i = 0; i < parts.length; i++)
            parts[i] = Character.toUpperCase(parts[i].charAt(0)) + parts[i].substring(1).toLowerCase();
        return String.join(" ", parts);
    }

    private final SortType sortType;

    public RowComparator(SortType sortType) {
        this.sortType = sortType;
    }

    public RowComparator(String sortTypeName) {
        this.sortType = SortType.valueOf(sortTypeName.toUpperCase().replace(" ", "_"));
    }

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

}