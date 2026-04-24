package org.troy.capstone.data_structures;

import java.util.List;
import java.util.TreeMap;

import org.troy.capstone.constants.TableColumnName;

import tech.tablesaw.api.Table;

/**
 * A data {@code TreeMap} holding keys of prices and values of item indices, allowing for efficient retrieval of items within a specified price range.
 */
public class PriceTree extends TreeMap<Float, Short> {

    //Main in EntryPoints.java to stop Javadocs from including it
    
    /**
     * Creates a {@code PriceTree} from a {@code Table}.
     *
     * @pre table is not null and contains the expected columns for prices and item indices.
     *
     * @param table A tablesaw {@code Table} containing the item data, with each row representing an item and containing a price column and an index column.
     */
    public PriceTree(Table table) {
        List<Float> prices = table.floatColumn(TableColumnName.PRICE.getColumnName()).asList();
        List<Short> itemIndices = table.shortColumn(TableColumnName.INDEX.getColumnName()).asList(); 
        addAllItems(prices, itemIndices);
    }

    /**
     * Adds an item to the {@code PriceTree} given its price and index.
     *
     * @pre price is a non-negative float representing the price of the item.
     *      itemIndex is a short representing the index of the item in the original table.
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
     * @pre {@code minPrice} and {@code maxPrice} are non-negative floats.
     *      {@code minPrice} is less than or equal to {@code maxPrice}.
     *
     * @param minPrice The minimum price of the range.
     * @param maxPrice The maximum price of the range.
     * @return An array of item indices that fall within the specified price range.
     */
    public int[] findItemsInPriceRange(float minPrice, float maxPrice) {
        return subMap(minPrice, true, maxPrice, true).values().stream().mapToInt(Short::intValue).toArray();
    }

    /**
     * Adds all items from lists of prices and indices to the {@code PriceTree}.
     *
     * @pre {@code prices} and {@code itemIndices} are not null.
     *      {@code prices} and {@code itemIndices} have the same size.
     *
     * @param prices A list of prices for the items to add.
     * @param itemIndices A list of item indices corresponding to the prices.
     */
    private void addAllItems(List<Float> prices, List<Short> itemIndices) {
        for (int i = 0; i < prices.size(); i++)
            addItem(prices.get(i), itemIndices.get(i));
    }

}