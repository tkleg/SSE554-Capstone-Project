package org.troy.capstone.search_engine;

import org.troy.capstone.data_structures.PriceTree;

import tech.tablesaw.api.Table;

/**
 * A class that provides functionality to filter items based on their price using a PriceTree data structure for efficient retrieval.
 */
public class PriceFilter {
    /** The PriceTree data structure used for efficient price-based filtering. */
    private final PriceTree priceTree;

    /**
     * Constructor for a PriceFilter from a Table.
     *
     * @pre table is not null and contains the expected columns for prices and item indices.
     *
     * @param table A tablesaw Table containing the item data.
     */
    public PriceFilter(Table table) {
        this.priceTree = new PriceTree(table);
    }

    /**
     * Filters items based on a specified price range.
     *
     * @pre minPrice and maxPrice are non-negative floats.
     *      minPrice is less than or equal to maxPrice.
     *
     * @param minPrice The minimum price of the range.
     * @param maxPrice The maximum price of the range.
     * @return An array of item indices that fall within the specified price range.
     */
    public int[] filterByPriceRange(float minPrice, float maxPrice) {
        return priceTree.findItemsInPriceRange(minPrice, maxPrice);
    }
}
