package org.troy.capstone.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.troy.capstone.annotations.Generated;
import org.troy.capstone.constants.DataPath;
import org.troy.capstone.constants.TableColumnName;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

/** Utility class for working with Tablesaw tables, including reading and writing CSV files. */
public class TableUtils {

    /**
     * Only exists to prevent Jacoco from complaining about the default constructor not being tested.
     * As the only function of this class is to provide static methods, there is no reason for it to be instantiated, so the constructor is private.
     */
    @Generated
    private TableUtils() {
    }

    /** Helper method to create CSV read options with specified path.
     * 
     * @param path The path to the CSV file.
     * @return CsvReadOptions configured with the specified path and column types.
     */
    private static CsvReadOptions createCsvReadOptions(String path) {
        return CsvReadOptions.builder(path)
            .columnTypesPartial(Map.of(
                TableColumnName.PRICE.getColumnName(), ColumnType.FLOAT,
                TableColumnName.REVIEW_SCORE.getColumnName(), ColumnType.FLOAT,
                TableColumnName.REVIEW_COUNT.getColumnName(), ColumnType.SHORT,
                TableColumnName.STOCK_QUANTITY.getColumnName(), ColumnType.SHORT,
                TableColumnName.INDEX.getColumnName(), ColumnType.SHORT
            )).build();
    }

    /**
     * Reads the data from a CSV file into a Tablesaw Table, and adds an index column to the table.
      * 
      * @pre The cleaned data CSV file exists with the right data.
      *
      * @param dataPath The DataPath enum value representing the path to the CSV file to be read.
     * @return A Tablesaw Table containing the data from the cleaned data CSV file, with an added index column.
     */
    public static Table readData(DataPath dataPath){
        CsvReadOptions options = createCsvReadOptions(dataPath.toString());
        Table table = Table.read().usingOptions(options);  
        insertIndexColumn(table);
        return table;
    }

    /**
     * Writes the cleaned data CSV file.
     * 
     * @param table The Tablesaw Table containing the data to be written.
     * @param dataPath The DataPath enum value representing the path to the CSV file to be written.
     *
     * @post The cleaned data CSV file is created at the specified path, containing the data from the provided table.
     */
    @Generated
    public static void writeData(Table table, DataPath dataPath){
        table.write().csv(dataPath.toString());
    }

    /** Inserts an index column into the given table, with values from 0 to rowCount-1.
     * 
     * @param table The Tablesaw Table to have the index column inserted. The table must not already contain a column with the name specified by TableColumnName.INDEX.
     */
    private static void insertIndexColumn(Table table) {
        int rowCount = table.rowCount();
        ShortColumn indexColumn = ShortColumn.create(
            TableColumnName.INDEX.getColumnName(), 
            IntStream.range(0, rowCount).mapToObj(i -> (short) i).toArray(Short[]::new)
        );
        table.addColumns( indexColumn );
    }

    /** Converts a Tablesaw Table to a list of Rows. Uses a loop instead of stream due to stream returning a list of the same row repeated.
     * 
     * @pre table is not null and contains at least one row. The rows in the table are properly formatted and contain the expected columns for the application.
     * @param table The Tablesaw Table to be converted.
     * @return A list of Rows representing the data in the table.
     */
    public static List<Row> tableToRowList(Table table) {
        List<Row> rows = new ArrayList<>();
        for( int i = 0; i < table.rowCount(); i++ )
            rows.add(table.row(i));
        return rows;
    }

}
