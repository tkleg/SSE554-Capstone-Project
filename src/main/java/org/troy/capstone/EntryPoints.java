package org.troy.capstone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.troy.capstone.search_engine.sorting.*;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.data_structures.ItemTable.ItemHashMap;
import org.troy.capstone.data_structures.ItemTable.SieveOfEratosthenes;
import org.troy.capstone.search_engine.QueryFilter;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class EntryPoints {

    public static void main(String[] args) throws Exception {
        System.out.println("""
        Enter the number for the main method to run:
        1: ItemHashMapMain
        2: SieveOfEratosthenesMain
        3: QueryFilterMain
        4: InsertionSortMain
        """);
        try (Scanner scan = new Scanner(System.in)) {
            String choice = scan.nextLine().trim();
            switch (choice) {
                case "1" -> ItemHashMapMain(args);
                case "2" -> SieveOfEratosthenesMain(args);
                case "3" -> QueryFilterMain(args);
                default -> System.out.println("Invalid choice. Please enter a number from 1 to 3.");
            }
        }
    }

    public static void ItemHashMapMain(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Table table = TableUtils.readCleanedAttributedData();
        ItemHashMap itemMap = ItemHashMap.fromTable(table);
        Method printBucketSizeCountsCustomVsBuiltIn = ItemHashMap.class.getDeclaredMethod("printBucketSizeCountsCustomVsBuiltIn");
        printBucketSizeCountsCustomVsBuiltIn.setAccessible(true);
        printBucketSizeCountsCustomVsBuiltIn.invoke(itemMap);
    }

    public static void SieveOfEratosthenesMain(String[] args) {
        SieveOfEratosthenes s = new SieveOfEratosthenes(100_000_000);
        Optional<Integer> primeUnder100mil = s.maxPrimeUnder100mil();
        if( primeUnder100mil.isPresent() )
            System.out.println("Largest prime under 100 million: " + primeUnder100mil.get());
        else
            System.out.println("No prime found under 100 million.");
    }

    public static void QueryFilterMain(String[] args) {
        
        Table table = TableUtils.readCleanedAttributedData();
        QueryFilter queryFilter = new QueryFilter(table);

        try (Scanner scan = new Scanner(System.in)) {
            while (true) {
                System.out.print("Enter search query (or 'exit' to quit): ");
                String userQuery = scan.nextLine().trim();
                if (userQuery.equalsIgnoreCase("exit"))
                    break;
                Map<String, Float> results = queryFilter.search(userQuery);
                for(Map.Entry<String, Float> entry : results.entrySet())
                    System.out.println("ID: " + entry.getKey() + ", Score: " + entry.getValue());
            }
        }
    }

    public static void InsertionSortMain(String[] args) {
        Table table = TableUtils.readCleanedAttributedData();
        LongWrapper time = new LongWrapper();
        List<Row> rows = new ArrayList<>();
        table.stream().forEach(rows::add);
        InsertionSort.insertionSort(rows, new RowComparator(RowComparator.SortType.PRICE_ASCENDING), time);
        Table sortedTable = Table.create("Sorted Table");
        rows.forEach(sortedTable::append);
        System.out.println("Time taken to sort: " + time.getValue() + " nanoseconds");
        System.out.println("Is sorted: " + SortingAnalysis.isSorted(sortedTable, new RowComparator(RowComparator.SortType.PRICE_ASCENDING)));
    }

    public static void SortingAnalysisMain(String[] args) throws Exception {
        Table table = TableUtils.readCleanedAttributedData();
        //Add a column of random numbers (relevance) to the main table
        int tableSize = table.rowCount();
        java.util.Random rand = new java.util.Random();
        FloatColumn relevanceColumn = FloatColumn.create(TableColumnName.RELEVANCE.getColumnName(), tableSize);
        for (int i = 0; i < tableSize; i++)
            relevanceColumn.set(i, rand.nextFloat());
        table.addColumns(relevanceColumn); // Add to main table

        //Create a performance table with the same number of rows as the main table
        Table performanceTable = Table.create("Performance Table");
        IntColumn tableSizeColumn = IntColumn.create("Table Size");
        StringColumn sortTypeColumn = StringColumn.create("Sort Type");
        StringColumn comparatorColumn = StringColumn.create("Comparator");
        LongColumn avgTimeColumn = LongColumn.create("Average Time (ns)");
        performanceTable.addColumns(tableSizeColumn, sortTypeColumn, comparatorColumn, avgTimeColumn);

        List<String> algorithms = List.of("quick", "insertion");
        for(RowComparator comparator : RowComparator.getComparators())
            for (String algorithm : algorithms)
                performanceTable = SortingAnalysis.analyzeSortingPerformance(table, performanceTable, algorithm, 5, comparator, 2);

        performanceTable.write().csv("sorting_performance.csv");
    }

}
