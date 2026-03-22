package org.troy.capstone.search_engine.sorting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.troy.capstone.constants.TableColumnName;

import tech.tablesaw.api.Row;

/** Class to hold comparators for sorting rows based on different criteria */
public class Comparators {

    public static final Comparator<Row> PRICE_ASCENDING = Comparator.comparingDouble(row -> row.getFloat(TableColumnName.PRICE.getColumnName()));
    public static final Comparator<Row> PRICE_DESCENDING = PRICE_ASCENDING.reversed();

    public static final Comparator<Row> RELEVANCE_ASCENDING = Comparator.comparingDouble(row -> row.getFloat(TableColumnName.RELEVANCE.getColumnName()));
    public static final Comparator<Row> RELEVANCE_DESCENDING = RELEVANCE_ASCENDING.reversed();

    public static final Comparator<Row> RATING_ASCENDING = Comparator.comparingDouble(row -> row.getFloat(TableColumnName.REVIEW_SCORE.getColumnName()));
    public static final Comparator<Row> RATING_DESCENDING = RATING_ASCENDING.reversed();

    private static final Map<Comparator<Row>, String> comparatorToName = Map.of(
        PRICE_ASCENDING, "Price Low to High",
        PRICE_DESCENDING, "Price High to Low",
        RELEVANCE_ASCENDING, "Relevance Low to High",
        RELEVANCE_DESCENDING, "Relevance High to Low",
        RATING_ASCENDING, "Rating Low to High",
        RATING_DESCENDING, "Rating High to Low"
    );

    private static final List<Comparator<Row>> COMPARATORS = comparatorToName.keySet().stream().toList();

    private static final List<String> COMPARATOR_NAMES = comparatorToName.values().stream().toList();

    private static final Map<String, Comparator<Row>> nameToComparator =
    comparatorToName.entrySet()
        .stream()
        .collect(Collectors.toMap(
            Map.Entry::getValue,
            Map.Entry::getKey
        ));

    public static String getNameByComparator(Comparator<Row> comparator) {
        return comparatorToName.getOrDefault(comparator, "Unknown Comparator");
    }

    public static List<Comparator<Row>> getComparators() {
        return new ArrayList<>(COMPARATORS);
    }

    public static List<String> getComparatorNames() {
        return new ArrayList<>(COMPARATOR_NAMES);
    }

    public static Comparator<Row> getComparatorByName(String name) {
        return nameToComparator.get(name);
    }

}
