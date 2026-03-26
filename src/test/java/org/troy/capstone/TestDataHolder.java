package org.troy.capstone;

import java.util.List;

import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.utils.TableUtils;
import tech.tablesaw.api.Table;

public class TestDataHolder {
    private static final Table table;
    private static final ItemHashMap itemHashMap;
    private static final List<String> allItemIds;

    static {
        table = TableUtils.readCleanedAttributedData();
        itemHashMap = ItemHashMap.fromTable(table);
        allItemIds = itemHashMap.getItemIdsAsList();
    }

    public static void main(String[] args) {
        System.out.println("Table loaded with " + table.rowCount() + " rows and " + table.columnCount() + " columns.");
        System.out.println("ItemHashMap contains " + itemHashMap.size() + " items.");
        System.out.println("First 5 item IDs: " + allItemIds.subList(0, 5));
    }

    public static Table getTableCopy() {
        return table.copy();
    }

    public static ItemHashMap getItemHashMapCopy() {
        return itemHashMap.copy();
    }

    public static List<String> getAllItemIdsCopy() {
        return List.copyOf(allItemIds);
    }
    
}