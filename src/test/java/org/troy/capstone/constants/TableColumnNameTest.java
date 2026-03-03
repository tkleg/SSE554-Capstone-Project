package org.troy.capstone.constants;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Table;

public class TableColumnNameTest {
    private static Table table;

    //Load table to allow checking for column names
    @BeforeAll
    public static void setup() {
        table = TableUtils.readCleanedAttributedData();
    }
    
    @ParameterizedTest
    @EnumSource(TableColumnName.class)
    public void testColumnNames(TableColumnName column) {
        assert table.columnNames().contains( column.getColumnName() ) : "Table should contain column: " + column.getColumnName();
    }
    
}
