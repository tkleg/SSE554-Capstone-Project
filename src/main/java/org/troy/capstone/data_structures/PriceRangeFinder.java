package org.troy.capstone.data_structures;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.troy.capstone.annotations.TestExclusionGenerated;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Table;

/**
 * A data structure for efficiently finding items within a specified price range.
 * Extends TreeMap with prices as keys and item indices as values.
 */
public class PriceRangeFinder extends TreeMap<Float, Short> {

    @TestExclusionGenerated
    /**
     * @hidden
     */
    public static void main(String[] args) {
        Table table = TableUtils.readCleanedData();
        PriceRangeFinder finder = new PriceRangeFinder(table);
        int[] itemsInRange = finder.findItemsInPriceRange(10.0f, 20.0f);
        System.out.println("Items in price range 10-20: " + Arrays.toString(itemsInRange));
    }
    
    /**
     * Creates a PriceRangeFinder from a Table.
     *
     * @pre <ul><li>table is not null and contains the expected columns for prices and item indices.</li></ul>
     *
     * @param table A tablesaw Table containing the item data, with each row representing an item and containing a price column and an index column.
     */
    public PriceRangeFinder(Table table) {
        List<Float> prices = table.floatColumn(TableColumnName.PRICE.getColumnName()).asList();
        List<Short> itemIndices = table.shortColumn(TableColumnName.INDEX.getColumnName()).asList(); 
        addAllItems(prices, itemIndices);
    }

    /**
     * Adds an item to the PriceRangeFinder given its price and index.
     *
     * @pre <ul><li>price is a non-negative float representing the price of the item.</li>
     *      <li>itemIndex is a short representing the index of the item in the original table.</li></ul>
     *
     * @param price The price of the item to add.
     * @param itemIndex The index of the item in the original table to add.
     */
    private void addItem(float price, Short itemIndex) {
        put(price, itemIndex);
    }

    /**
     * Finds items within a specified price range.
     *
     * @pre <ul><li>minPrice and maxPrice are non-negative floats.</li>
     *      <li>minPrice is less than or equal to maxPrice.</li></ul>
     *
     * @param minPrice The minimum price of the range.
     * @param maxPrice The maximum price of the range.
     * @return An array of item indices that fall within the specified price range.
     */
    public int[] findItemsInPriceRange(float minPrice, float maxPrice) {
        return subMap(minPrice, true, maxPrice, true).values().stream().mapToInt(Short::intValue).toArray();
    }

    /**
     * Adds all items from lists of prices and indices to the PriceRangeFinder.
     *
     * @pre <ul><li>prices and itemIndices are not null.</li>
     *      <li>prices and itemIndices have the same size.</li></ul>
     *
     * @param prices A list of prices for the items to add.
     * @param itemIndices A list of item indices corresponding to the prices.
     */
    private void addAllItems(List<Float> prices, List<Short> itemIndices) {
        for (int i = 0; i < prices.size(); i++)
            addItem(prices.get(i), itemIndices.get(i));
    }

}