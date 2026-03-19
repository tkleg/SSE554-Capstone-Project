package org.troy.capstone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.troy.capstone.data_structures.PriceRangeFinder;
import org.troy.capstone.data_structures.ItemTable.ItemHashMap;
import org.troy.capstone.data_structures.ItemTable.SieveOfEratosthenes;
import org.troy.capstone.search_engine.QueryFilter;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Table;

public class EntryPoints {

    public static void main(String[] args) throws Exception {
        System.out.println("""
        Enter the number for the main method to run:
        1: PriceRangeFinderMain
        2: ItemHashMapMain
        3: SieveOfEratosthenesMain
        4: QueryFilterMain
        """);
        try (Scanner scan = new Scanner(System.in)) {
            String choice = scan.nextLine().trim();
            switch (choice) {
                case "1" -> PriceRangeFinderMain(args);
                case "2" -> ItemHashMapMain(args);
                case "3" -> SieveOfEratosthenesMain(args);
                case "4" -> QueryFilterMain(args);
                default -> System.out.println("Invalid choice. Please enter a number from 1 to 4.");
            }
        }
    }

    public static void PriceRangeFinderMain(String[] args) {
        Table table = TableUtils.readCleanedData();
        PriceRangeFinder finder = new PriceRangeFinder(table);
        int[] itemsInRange = finder.findItemsInPriceRange(10.0f, 20.0f);
        System.out.println("Items in price range 10-20: " + Arrays.toString(itemsInRange));
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

    



    
}
