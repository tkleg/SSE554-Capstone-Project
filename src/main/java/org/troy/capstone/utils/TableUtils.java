package org.troy.capstone.utils;

import javax.swing.JTable;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

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
}
