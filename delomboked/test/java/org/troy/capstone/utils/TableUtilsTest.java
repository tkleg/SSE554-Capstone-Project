package org.troy.capstone.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.troy.capstone.constants.DataPath;
import org.troy.capstone.constants.TableColumnName;

import tech.tablesaw.api.Table;

public class TableUtilsTest {

    private static List<TableColumnName> expectedColumns;

    @BeforeAll
    public static void setup() {
        expectedColumns = new ArrayList<>(Arrays.asList(TableColumnName.values()));
        expectedColumns.remove(TableColumnName.RELEVANCE); //Relevance is not a column in the original dataset when read, but is added later during search query filtering
    }
    
    @ParameterizedTest
    @DisplayName("Test columns and size of data table with all data path options")
    @EnumSource(value = DataPath.class, names = {"ROOT"}, mode = EnumSource.Mode.EXCLUDE)
    public void testReadData(DataPath dataPath) {
        Table table = switch(dataPath){
            case CLEANED_DATA_CSV -> TableUtils.readCleanedData();
            case CLEANED_DATA_CSV_LONG -> TableUtils.readCleanedDataLongPath();
            case ATTRIBUTED_DATA_CSV -> TableUtils.readAttributedData();
            case ATTRIBUTED_DATA_CSV_LONG -> TableUtils.readAttributedDataLongPath();
            case CLEANED_ATTRIBUTED_DATA_CSV -> TableUtils.readCleanedAttributedData();
            case CLEANED_ATTRIBUTED_DATA_CSV_LONG -> TableUtils.readCleanedAttributedDataLongPath();
            default -> throw new IllegalArgumentException("Unexpected DataPath value: " + dataPath + "Expected one of the specific data paths, not ROOT");
        };
        assertNotNull(table, "Table should not be null");
        //The cleaned attributed data has 1000 rows, since some rows were not able to be attributed
        if( dataPath == DataPath.CLEANED_DATA_CSV || dataPath == DataPath.CLEANED_DATA_CSV_LONG )
            assertEquals(1000, table.rowCount(), "Cleaned Attributed Data Table should have 1000 rows");
        else
            assertEquals(961, table.rowCount(), "Table should have 961 rows");
        assertTrue(table.columnNames().containsAll(expectedColumns.stream().map(TableColumnName::getColumnName).toList()), "Table should contain expected columns");
        assertEquals(15, table.columnCount(), "Table should have 15 columns");
    }
}
