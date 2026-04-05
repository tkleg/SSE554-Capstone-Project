package org.troy.capstone.search_engine.sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.troy.capstone.search_engine.sorting.comparator.RowComparator;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class SortingAnalysis {

    public static boolean isSorted(Table table, RowComparator comparator) {
        for (int i = 1; i < table.rowCount(); i++) {
            if (comparator.compare(table.row(i - 1), table.row(i)) > 0)
                return false;
        }
        return true;
    }

    public static void testSorts(Table table) {
        for (RowComparator.SortType sortType : RowComparator.SortType.values()) {
            RowComparator comparator = new RowComparator(sortType);
            LongWrapper time = new LongWrapper();
            Table sortedTable = Sorter.sortTable(table, comparator, time);
            if (!isSorted(sortedTable, comparator))
                System.out.println("Sorting failed for comparator: " + comparator.toString() + " in " + time.getValue() + " nanoseconds");
            else
                System.out.println("Sorting succeeded for comparator: " + comparator.toString() + " in " + time.getValue() + " nanoseconds");
        }
    }

    public static Table shuffleTable(Table table) {
        List<Row> rows = TableUtils.tableToRowList(table);
        Collections.shuffle(rows);
        Table shuffledTable = table.emptyCopy();
        rows.forEach(shuffledTable::append);
        return shuffledTable;
    }

    public static List<LongWrapper> removeOutliers(List<LongWrapper> times) {
        if (times.size() < 4) {
            return times; // Not enough data to remove outliers
        }
        List<Long> values = new ArrayList<>();
        for (LongWrapper time : times) {
            values.add(time.getValue());
        }
        Collections.sort(values);
        long q1 = values.get(values.size() / 4);
        long q3 = values.get(3 * values.size() / 4);
        long iqr = q3 - q1;
        long lowerBound = q1 - (long)(1.5 * iqr);
        long upperBound = q3 + (long)(1.5 * iqr);

        List<LongWrapper> filteredTimes = new ArrayList<>();
        for (LongWrapper time : times)
            if (time.getValue() >= lowerBound && time.getValue() <= upperBound)
                filteredTimes.add(time);

        return filteredTimes;
    }

    public static Table analyzeSortingPerformance(Table table, Table performanceTable, String algorithm, int numberOfTrials, RowComparator comparator, int step ) throws Exception {
        IntColumn tableSizeColumn = performanceTable.intColumn("Table Size");
        StringColumn algorithmColumn = performanceTable.stringColumn("Algorithm");
        StringColumn comparatorColumn = performanceTable.stringColumn("Comparator");
        LongColumn avgTimeColumn = performanceTable.longColumn("Average Time (ns)");

        for (int size = 1; size <= table.rowCount(); size += step) {
            Table subset = shuffleTable(table.inRange(0, Math.min(size, table.rowCount())));
            List<Row> rows = TableUtils.tableToRowList(subset);
            List<LongWrapper> timeArr = new ArrayList<>(numberOfTrials);
            LongWrapper time = new LongWrapper();
            for (int x = 0; x < numberOfTrials; x++) {
                timeArr.add(new LongWrapper());
                switch (algorithm) {
                    case "quick" -> QuickSort.quickSort(rows, comparator, timeArr.get(x));
                    case "insertion" -> InsertionSort.insertionSort(rows, comparator, timeArr.get(x));
                    case "mixed" -> Sorter.mixedSort(rows, comparator, timeArr.get(x));
                    default -> throw new IllegalArgumentException("Unsupported sorting algorithm: " + algorithm);
                }
            }
            Long sum = 0L;
            List<LongWrapper> filteredTimes = removeOutliers(timeArr);
            for (int x = 0; x < filteredTimes.size(); x++)
                sum += filteredTimes.get(x).getValue();
            time.setValue(sum / filteredTimes.size());
            System.out.println("Algorithm: " + algorithm + " - Comparator: " + comparator.toString() + " - Avg Time " + size + " items: " + time.getValue() + " nanoseconds");

            //Add a new row to the performance table
            tableSizeColumn.append(size);
            algorithmColumn.append(algorithm);
            comparatorColumn.append(comparator.toString());
            avgTimeColumn.append(time.getValue());
        }
        return performanceTable;
    }


}
