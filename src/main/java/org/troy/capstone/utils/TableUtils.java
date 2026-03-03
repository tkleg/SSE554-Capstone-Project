package org.troy.capstone.utils;

import java.util.Map;
import java.util.stream.IntStream;

import javax.swing.JTable;

import org.troy.capstone.constants.dataPaths;
import org.troy.capstone.constants.tableColumns;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

public class TableUtils {

    // Base column type mapping for CSV reading
    private static final Map<String, ColumnType> COLUMN_TYPES = Map.of(
        tableColumns.PRICE.getColumnName(), ColumnType.FLOAT,
        tableColumns.REVIEW_SCORE.getColumnName(), ColumnType.FLOAT,
        tableColumns.REVIEW_COUNT.getColumnName(), ColumnType.SHORT,
        tableColumns.STOCK_QUANTITY.getColumnName(), ColumnType.SHORT
    );

    // Helper method to create CSV read options with specified path
    private static CsvReadOptions createCsvReadOptions(String path) {
        return CsvReadOptions.builder(path)
            .columnTypesPartial(COLUMN_TYPES)
            .build();
    }

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

    public static Table readCleanedData() {
        CsvReadOptions options = createCsvReadOptions(dataPaths.CLEANED_DATA_CSV);
        Table table = Table.read().usingOptions(options);  
        insertIndexColumn(table);
        return table;
    }

    public static Table readCleanedDataLongPath() {
        CsvReadOptions options = createCsvReadOptions(dataPaths.CLEANED_DATA_CSV_LONG);
        Table table = Table.read().usingOptions(options);  
        insertIndexColumn(table);
        return table;
    }

    public static Table readAttributedData(){
        CsvReadOptions options = createCsvReadOptions(dataPaths.ATTRIBUTED_DATA_CSV);
        Table table = Table.read().usingOptions(options);
        insertIndexColumn(table);
        return table;
    }

    public static Table readAttributedDataLongPath(){
        CsvReadOptions options = createCsvReadOptions(dataPaths.ATTRIBUTED_DATA_CSV_LONG);
        Table table = Table.read().usingOptions(options);
        insertIndexColumn(table);
        return table;
    }

    public static Table readCleanedAttributedData(){
        CsvReadOptions options = createCsvReadOptions(dataPaths.CLEANED_ATTRIBUTED_DATA_CSV);
        Table table = Table.read().usingOptions(options);
        insertIndexColumn(table);
        return table;
    }

    public static Table readCleanedAttributedDataLongPath(){
        CsvReadOptions options = createCsvReadOptions(dataPaths.CLEANED_ATTRIBUTED_DATA_CSV_LONG);
        Table table = Table.read().usingOptions(options);
        insertIndexColumn(table);
        return table;
    }
    
    public static void writeCleanedData(Table table) {
        table.write().csv(dataPaths.CLEANED_DATA_CSV);
    }

    public static void writeCleanedDataLongPath(Table table) {
        table.write().csv(dataPaths.CLEANED_DATA_CSV_LONG);
    }

    public static void writeAttributedData(Table table) {
        table.write().csv(dataPaths.ATTRIBUTED_DATA_CSV);
    }

    public static void writeAttributedDataLongPath(Table table) {
        table.write().csv(dataPaths.ATTRIBUTED_DATA_CSV_LONG);
    }

    public static void writeCleanedAttributedData(Table table) {
        table.write().csv(dataPaths.CLEANED_ATTRIBUTED_DATA_CSV);
    }

    public static void writeCleanedAttributedDataLongPath(Table table) {
        table.write().csv(dataPaths.CLEANED_ATTRIBUTED_DATA_CSV_LONG);
    }
    
    private static void insertIndexColumn(Table table) {
        int rowCount = table.rowCount();
        ShortColumn indexColumn = ShortColumn.create(
            tableColumns.INDEX.getColumnName(), 
            IntStream.range(0, rowCount).mapToObj(i -> (short) i).toArray(Short[]::new)
        );
        table.addColumns( indexColumn );
    }
}
