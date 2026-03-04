package org.troy.capstone.data_structures;

import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.troy.capstone.annotations.TestExclusionGenerated;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Table;

public class PriceRangeFinder extends TreeMap<Float, Short> {

    @TestExclusionGenerated
    public static void main(String[] args) {
        Table table = TableUtils.readCleanedData();
        PriceRangeFinder finder = new PriceRangeFinder(table);
        int[] itemsInRange = finder.findItemsInPriceRange(10.0f, 20.0f);
        System.out.println("Items in price range 10-20: " + Arrays.toString(itemsInRange));
    }
    
    /**
     * Creates a PriceRangeFinder from a Table.
     *
     * pre-conditions: table is not null and contains the expected columns for prices and item indices.
     *
     * @param table (Table): a tablesaw Table containing the item data, with each row representing an item and containing a price column and an index column.
     * @return finder (PriceRangeFinder): a PriceRangeFinder containing all items from the table, with prices as keys and item indices as values.
     */
    public PriceRangeFinder(Table table) {
        List<Float> prices = table.floatColumn(TableColumnName.PRICE.getColumnName()).asList();
        List<Short> itemIndices = table.shortColumn(TableColumnName.INDEX.getColumnName()).asList(); 
        addAllItems(prices, itemIndices);
    }

    /**
     * Adds an item to the PriceRangeFinder given its price and index.
     *
     * pre-conditions: price is a non-negative float representing the price of the item, and itemIndex is a short representing the index of the item in the original table.
     *
     * @param price (float): the price of the item to add
     * @param itemIndex (short): the index of the item in the original table to add
     */
    private void addItem(float price, Short itemIndex) {
        put(price, itemIndex);
    }

    /**
     * Finds items within a specified price range.
     *
     * pre-conditions: minPrice and maxPrice are non-negative floats, and minPrice is less than or equal to maxPrice.
     *
     * @param minPrice (float): the minimum price of the range
     * @param maxPrice (float): the maximum price of the range
     * @return itemsInRange (int[]): an array of item indices that fall within the specified price range
     */
    public int[] findItemsInPriceRange(float minPrice, float maxPrice) {
        return subMap(minPrice, true, maxPrice, true).values().stream().mapToInt(Short::intValue).toArray();
    }

    /**
     * Adds all items from lists of prices and indices to the PriceRangeFinder.
     *
     * pre-conditions: prices and itemIndices are not null and have the same size.
     *
     * @param prices (List<Float>): a list of prices for the items to add
     * @param itemIndices (List<Short>): a list of item indices corresponding to the prices
     */
    private void addAllItems(List<Float> prices, List<Short> itemIndices) {
        for (int i = 0; i < prices.size(); i++)
            addItem(prices.get(i), itemIndices.get(i));
    }

}
