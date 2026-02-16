package org.troy.capstone.data_structures;

import java.util.List;
import java.util.TreeMap;

import tech.tablesaw.api.Table;

public class PriceRangeFinder extends TreeMap<Float, Short> {

    public PriceRangeFinder(Table table) {
        addItems(table);
    }

    private void addItem(float price, short itemId) {
        put(price, itemId);
    }

    public List<Short> findItemsInPriceRange(float minPrice, float maxPrice) {
        return subMap(minPrice, true, maxPrice, true).values().stream().toList();
    }

    private void addAllItems(List<Float> prices, List<Short> itemIds) {
        for (int i = 0; i < prices.size(); i++)
            addItem(prices.get(i), itemIds.get(i));
    }

    public void addItems(Table table) {
        List<Float> prices = table.floatColumn("price").asList();
        List<Short> itemIds = table.shortColumn("id").asList(); 
        addAllItems(prices, itemIds);
    }
}
