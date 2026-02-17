package org.troy.capstone.data_structures;

import java.util.List;
import java.util.TreeMap;

import org.troy.capstone.annotations.TestExclusionGenerated;
import org.troy.capstone.constants.tableColumns;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Table;

public class PriceRangeFinder extends TreeMap<Float, String> {

    @TestExclusionGenerated
    public static void main(String[] args) {
        Table table = TableUtils.readCleanedData();
        PriceRangeFinder finder = new PriceRangeFinder(table);
        List<String> itemsInRange = finder.findItemsInPriceRange(10.0f, 20.0f);
        System.out.println("Items in price range 10-20: " + itemsInRange);
    }
    
    public PriceRangeFinder(Table table) {
        addAllItems(table);
    }

    private void addItem(float price, String itemId) {
        put(price, itemId);
    }

    public List<String> findItemsInPriceRange(float minPrice, float maxPrice) {
        return subMap(minPrice, true, maxPrice, true).values().stream().toList();
    }

    private void addAllItems(List<Float> prices, List<String> itemIds) {
        for (int i = 0; i < prices.size(); i++)
            addItem(prices.get(i), itemIds.get(i));
    }

    public void addAllItems(Table table) {
        List<Float> prices = table.floatColumn(tableColumns.PRICE.getColumnName()).asList();
        List<String> itemIds = table.stringColumn(tableColumns.ID.getColumnName()).asList(); 
        addAllItems(prices, itemIds);
    }
}
