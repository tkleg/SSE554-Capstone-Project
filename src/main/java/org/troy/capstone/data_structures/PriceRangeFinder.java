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
    
    public PriceRangeFinder(Table table) {
        addAllItems(table);
    }

    private void addItem(float price, Short itemIndex) {
        put(price, itemIndex);
    }

    public int[] findItemsInPriceRange(float minPrice, float maxPrice) {
        return subMap(minPrice, true, maxPrice, true).values().stream().mapToInt(Short::intValue).toArray();
    }

    private void addAllItems(List<Float> prices, List<Short> itemIndices) {
        for (int i = 0; i < prices.size(); i++)
            addItem(prices.get(i), itemIndices.get(i));
    }

    public void addAllItems(Table table) {
        List<Float> prices = table.floatColumn(TableColumnName.PRICE.getColumnName()).asList();
        List<Short> itemIndices = table.shortColumn(TableColumnName.INDEX.getColumnName()).asList(); 
        addAllItems(prices, itemIndices);
    }
}
