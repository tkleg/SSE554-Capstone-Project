package org.troy.capstone.utils;

import javax.swing.JTable;

import tech.tablesaw.api.Table;

public class TableUtils {

    public static Object[][] to2DArray(Table table) {
        
        // Setup 2D array
        int rowCount = (int) table.rowCount();
        int colCount = table.columnCount();
        Object[][] data = new Object[rowCount][colCount];

        // Populate 2D array with Table data
        for (int r = 0; r < rowCount; r++)
            for (int c = 0; c < colCount; c++)
                data[r][c] = table.get(r, c);

        return data;
    }

    public static JTable toJTable(Table table) {
        Object[] columnNames = table.columnNames().toArray(new Object[0]);
        Object[][] data = to2DArray(table);
        return new JTable(data, columnNames);
    }

}
