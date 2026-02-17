package org.troy.capstone.data_structures;

import java.util.List;
import java.util.TreeMap;

import org.troy.capstone.annotations.Generated;
import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Table;

public class PriceRangeFinder extends TreeMap<Float, Short> {

    @Generated
    public static void main(String[] args) {
        Table table = TableUtils.readCleanedData();
        PriceRangeFinder finder = new PriceRangeFinder(table);
        List<Short> itemsInRange = finder.findItemsInPriceRange(10.0f, 20.0f);
        System.out.println("Items in price range 10-20: " + itemsInRange);
    }
    
    public PriceRangeFinder(Table table) {
        addAllItems(table);
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

    public void addAllItems(Table table) {
        List<Float> prices = table.floatColumn(tableColumns.PRICE.getColumnName()).asList();
        List<Short> itemIds = table.shortColumn(tableColumns.ID.getColumnName()).asList(); 
        addAllItems(prices, itemIds);
    }
}
