package org.troy.capstone.search_engine.sorting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

/**
 * Code is sourced from a MindTap exercise from the course, but modified to fit the project.
 * 
 * Class provides QuickSort implementation for sorting tables based on a specified comparator.
 */
public class QuickSort {
    public static Table quickSort(Table table, Comparator<Row> comparator) {
        return quickSort(table, comparator, null);
    }

    public static Table quickSort(Table table, Comparator<Row> comparator, LongWrapper time) {
        long start = System.nanoTime();
        List<Row> rows = new ArrayList<>(table.stream().toList());
        quickSort(rows, 0, table.rowCount() - 1, comparator);
        Table newTable = table.emptyCopy();
        rows.forEach(newTable::append);
        if (time != null) {
            long end = System.nanoTime();
            time.setValue(end - start);
        }
        return newTable;
    }

    private static void quickSort(List<Row> rows, int low, int high, Comparator<Row> comparator) {
        if (low < high) {
            int pivot = partition(rows, low, high, comparator);
            quickSort(rows, low, pivot - 1, comparator);
            quickSort(rows, pivot + 1, high, comparator);
        }
    }

    private static int partition(List<Row> rows, int low, int high, Comparator<Row> comparator) {
        Row pivot = rows.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare(rows.get(j), pivot) <= 0) {
                i++;
                swap(rows, i, j);
            }
        }
        swap(rows, i + 1, high);
        return i + 1;
    }

    private static void swap(List<Row> rows, int i, int j) {
        Row temp = rows.get(i);
        rows.set(i, rows.get(j));
        rows.set(j, temp);
    }

    public static void main(String[] args) throws Exception {
        /*Table table = TableUtils.readCleanedAttributedData();
        File quickSortDataFile = new File("quick_sort_data.csv");
        quickSortDataFile.delete(); // Ensure we start with a clean file
        quickSortDataFile.createNewFile();
        quickSortDataFile.setWritable(true);
        try (PrintWriter writer = new PrintWriter(quickSortDataFile)) {
            writer.println("Size,AverageTime(ns)");
            int numberOfTrials = 20;
            for( int size = 1; size <= table.rowCount(); size++) {
                Table subset = Sorter.shuffleTable(table.inRange(0, Math.min(size, table.rowCount())));
                List<LongWrapper> timeArr = new ArrayList<>(numberOfTrials);
                LongWrapper time = new LongWrapper();
                for( int x = 0; x < numberOfTrials; x++) {
                    timeArr.add(new LongWrapper());
                    QuickSort.quickSort(subset, Comparators.getComparators().get(0), timeArr.get(x));
                }
                Long sum = 0L;
                List<LongWrapper> filteredTimes = Sorter.removeOutliers(timeArr);
                for( int x = 0; x < filteredTimes.size(); x++ )
                    sum += filteredTimes.get(x).getValue();
                
                time.setValue(sum / filteredTimes.size());
                System.out.println("Average time to sort " + size + " items: " + time.getValue() + " nanoseconds");
                writer.println(size + "," + time.getValue());
            }
            writer.flush();
        }*/
       
    }

}
