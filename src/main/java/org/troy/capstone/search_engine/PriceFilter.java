package org.troy.capstone.search_engine;

import org.troy.capstone.data_structures.PriceTree;

import tech.tablesaw.api.Table;

/**
 * A class that provides functionality to filter items based on their price using a {@code PriceTree} data structure for efficient retrieval.
 */
public class PriceFilter {
    /** The {@code PriceTree} data structure used for efficient price-based filtering. */
    private final PriceTree priceTree;

    /**
     * Constructor for a {@code PriceFilter} from a {@code Table}.
     *
     * @pre table is not null and contains the expected columns for prices and item indices.
     *
     * @param table A {@code Table} containing the item data.
     */
    public PriceFilter(Table table) {
        this.priceTree = new PriceTree(table);
    }

    /**
     * Filters items based on a specified price range.
     *
     * @pre {@code minPrice} and {@code maxPrice} are non-negative floats.
     *      {@code minPrice} is less than or equal to {@code maxPrice}.
     *
     * @param minPrice The minimum price of the range.
     * @param maxPrice The maximum price of the range.
     * @return An array of item indices that fall within the specified price range.
     */
    public int[] filterByPriceRange(float minPrice, float maxPrice) {
        return priceTree.findItemsInPriceRange(minPrice, maxPrice);
    }
}
