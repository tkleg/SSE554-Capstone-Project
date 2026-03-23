package org.troy.capstone.search_engine.sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class Sorter {

    public static boolean isSorted(Table table, Comparator<Row> comparator) {
        for (int i = 1; i < table.rowCount(); i++) {
            if (comparator.compare(table.row(i - 1), table.row(i)) > 0) {
                return false;
            }
        }
        return true;
    }

    public static void testSorts(Table table) {
        List<Comparator<Row>> comparators = Comparators.getComparators();
        for (Comparator<Row> comparator : comparators) {
            LongWrapper time = new LongWrapper();
            List<Row> rows = new ArrayList<>();
            table.stream().forEach(rows::add);
            QuickSort.quickSort(rows, comparator, time);
            Table sortedTable = table.emptyCopy();
            rows.forEach(sortedTable::append);
            if (!isSorted(sortedTable, comparator))
                System.out.println("Sorting failed for comparator: " + Comparators.getNameByComparator(comparator)+ " in " + time + " seconds");
            else
                System.out.println("Sorting succeeded for comparator: " + Comparators.getNameByComparator(comparator)+ " in " + time + " seconds");
        }
    }

    public static Table sortTable(Table table, String sortingOption){
        Comparator<Row> comparator = Comparators.getComparatorByName(sortingOption);
        System.out.println("Sorting using " + Comparators.getNameByComparator(comparator) + " comparator...");
        List<Row> rows = TableUtils.tableToRowList(table);
        if( rows.size() <= 25 ){
            System.out.println("Using Insertion Sort for small table...");
            InsertionSort.insertionSort(rows, comparator);
        } else {
            System.out.println("Using Quick Sort for larger table...");
            QuickSort.quickSort(rows, comparator);
        }
        Table sortedTable = table.emptyCopy();
        rows.forEach(sortedTable::append);
        return sortedTable;
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

    public static Table analyzeSortingPerformance(Table table, Table performanceTable, String algorithm, int numberOfTrials, Comparator<Row> comparator, int step ) throws Exception {
        IntColumn tableSizeColumn = performanceTable.intColumn("Table Size");
        StringColumn sortTypeColumn = performanceTable.stringColumn("Sort Type");
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
                    default -> throw new IllegalArgumentException("Unsupported sorting algorithm: " + algorithm);
                }
            }
            Long sum = 0L;
            List<LongWrapper> filteredTimes = removeOutliers(timeArr);
            for (int x = 0; x < filteredTimes.size(); x++)
                sum += filteredTimes.get(x).getValue();
            time.setValue(sum / filteredTimes.size());
            System.out.println("Algorithm: " + algorithm + " - Comparator: " + Comparators.getNameByComparator(comparator) + " - Avg Time " + size + " items: " + time.getValue() + " nanoseconds");

            //Add a new row to the performance table
            tableSizeColumn.append(size);
            sortTypeColumn.append(algorithm);
            comparatorColumn.append(Comparators.getNameByComparator(comparator));
            avgTimeColumn.append(time.getValue());
        }
        return performanceTable;
    }

    public static void main(String[] args) throws Exception {
        Table table = TableUtils.readCleanedAttributedData().first(50);
        Table sortedTable = sortTable(table, Comparators.getNameByComparator(Comparators.PRICE_ASCENDING));
        for(int i = 0; i < sortedTable.rowCount(); i++)
            System.out.println(sortedTable.row(i).getFloat(TableColumnName.PRICE.getColumnName()));
        /*Table table = TableUtils.readCleanedAttributedData();
        // Add a column of random numbers (relevance) to the main table
        int tableSize = table.rowCount();
        java.util.Random rand = new java.util.Random();
        FloatColumn relevanceColumn = FloatColumn.create(TableColumnName.RELEVANCE.getColumnName(), tableSize);
        for (int i = 0; i < tableSize; i++)
            relevanceColumn.set(i, rand.nextFloat());
        table.addColumns(relevanceColumn); // Add to main table

        // Create a performance table with the same number of rows as the main table
        Table performanceTable = Table.create("Performance Table");
        IntColumn tableSizeColumn = IntColumn.create("Table Size");
        StringColumn sortTypeColumn = StringColumn.create("Sort Type");
        StringColumn comparatorColumn = StringColumn.create("Comparator");
        LongColumn avgTimeColumn = LongColumn.create("Average Time (ns)");
        performanceTable.addColumns(tableSizeColumn, sortTypeColumn, comparatorColumn, avgTimeColumn);

        // Optionally, fill the columns with values
        // for (int i = 0; i < tableSize; i++) {
        //     tableSizeColumn.set(i, tableSize);
        //     sortTypeColumn.set(i, "quick");
        // }

        List<String> algorithms = List.of("quick", "insertion");
        for(Comparator<Row> comparator : Comparators.getComparators())
            for (String algorithm : algorithms)
                performanceTable = analyzeSortingPerformance(table, performanceTable, algorithm, 5, comparator, 2);

        performanceTable.write().csv("sorting_performance.csv");
        */
    }
}
