package org.troy.capstone.utils;

import java.util.Map;
import java.util.stream.IntStream;

import org.troy.capstone.annotations.TestExclusionGenerated;
import org.troy.capstone.constants.DataPath;
import org.troy.capstone.constants.TableColumnName;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

public class TableUtils {

    //Never called, just prevents Jacoco from complaining about missing code coverage for the default constructor
    @TestExclusionGenerated
    private TableUtils() {
    }

    // Helper method to create CSV read options with specified path
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
    */
    //Used in a very old version in order to test the dependencies tablesaw and javaSwing
    /*public static JTable toJTable(Table table) {
        Object[] columnNames = table.columnNames().toArray(new Object[0]);
        Object[][] data = to2DArray(table);
        return new JTable(data, columnNames);
    }*/

    //Used in a very old version in order to test the dependencies tablesaw and javafx
    /*
    public static TableView<ObservableList<Object>> tablesawTableToTableView(Table table) {
        TableView<ObservableList<Object>> tableView = new TableView<>();
        for( int column = 0; column < table.columnCount(); column++ ) {
            final int colIndex = column;
            Column<?> col = table.column(column);
            TableColumn<ObservableList<Object>, Object> tableColumn = new TableColumn<>(col.name());
            tableColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>( data.getValue().get(colIndex) ));
            tableView.getColumns().add(tableColumn);
        }

        ObservableList<ObservableList<Object>> data = FXCollections.observableArrayList();
        for( int row = 0; row < table.rowCount(); row++ ) {
            ObservableList<Object> rowData = FXCollections.observableArrayList();
            for( int col = 0; col < table.columnCount(); col++ )
                rowData.add( table.get(row, col) );
            data.add(rowData);
        }
        tableView.setItems(data);
        
        return tableView;
    }
    */

    public static Table readCleanedData() {
        CsvReadOptions options = createCsvReadOptions(DataPath.CLEANED_DATA_CSV.toString());
        Table table = Table.read().usingOptions(options);  
        insertIndexColumn(table);
        return table;
    }

    public static Table readCleanedDataLongPath() {
        CsvReadOptions options = createCsvReadOptions(DataPath.CLEANED_DATA_CSV_LONG.toString());
        Table table = Table.read().usingOptions(options);  
        insertIndexColumn(table);
        return table;
    }

    public static Table readAttributedData(){
        CsvReadOptions options = createCsvReadOptions(DataPath.ATTRIBUTED_DATA_CSV.toString());
        Table table = Table.read().usingOptions(options);
        insertIndexColumn(table);
        return table;
    }

    public static Table readAttributedDataLongPath(){
        CsvReadOptions options = createCsvReadOptions(DataPath.ATTRIBUTED_DATA_CSV_LONG.toString());
        Table table = Table.read().usingOptions(options);
        insertIndexColumn(table);
        return table;
    }

    public static Table readCleanedAttributedData(){
        CsvReadOptions options = createCsvReadOptions(DataPath.CLEANED_ATTRIBUTED_DATA_CSV.toString());
        Table table = Table.read().usingOptions(options);
        insertIndexColumn(table);
        return table;
    }

    public static Table readCleanedAttributedDataLongPath(){
        CsvReadOptions options = createCsvReadOptions(DataPath.CLEANED_ATTRIBUTED_DATA_CSV_LONG.toString());
        Table table = Table.read().usingOptions(options);
        insertIndexColumn(table);
        return table;
    }

    @TestExclusionGenerated
    public static void writeCleanedData(Table table) {
        table.write().csv(DataPath.CLEANED_DATA_CSV.toString());
    }

    @TestExclusionGenerated
    public static void writeCleanedDataLongPath(Table table) {
        table.write().csv(DataPath.CLEANED_DATA_CSV_LONG.toString());
    }

    @TestExclusionGenerated
    public static void writeAttributedData(Table table) {
        table.write().csv(DataPath.ATTRIBUTED_DATA_CSV.toString());
    }

    @TestExclusionGenerated
    public static void writeAttributedDataLongPath(Table table) {
        table.write().csv(DataPath.ATTRIBUTED_DATA_CSV_LONG.toString());
    }

    @TestExclusionGenerated
    public static void writeCleanedAttributedData(Table table) {
        table.write().csv(DataPath.CLEANED_ATTRIBUTED_DATA_CSV.toString());
    }

    @TestExclusionGenerated
    public static void writeCleanedAttributedDataLongPath(Table table) {
        table.write().csv(DataPath.CLEANED_ATTRIBUTED_DATA_CSV_LONG.toString());
    }
    
    private static void insertIndexColumn(Table table) {
        int rowCount = table.rowCount();
        ShortColumn indexColumn = ShortColumn.create(
            TableColumnName.INDEX.getColumnName(), 
            IntStream.range(0, rowCount).mapToObj(i -> (short) i).toArray(Short[]::new)
        );
        table.addColumns( indexColumn );
    }
}
