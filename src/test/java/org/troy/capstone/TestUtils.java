package org.troy.capstone;

import org.testfx.api.FxRobot;
import org.troy.capstone.constants.TestFXId;

import javafx.scene.Node;
import tech.tablesaw.api.Table;

public class TestUtils {
    

    /** 
     * Looks up a node by TestFXId and returns the result of the query.
     * @pre testFXId should correspond to a valid TestFXId for a node in the scene graph. The node should be properly initialized and added to the scene graph before this method is called.
     * @param testFXId The TestFXId of the node to look up.
     * @return The node corresponding to the given TestFXId, cast to the appropriate type.
     * @throws ClassCastException if the node found by the TestFXId cannot be cast to the expected type.
     * @throws EmptyNodeQueryException if no node is found.
     */
    public static <T extends Node> T lookupByTestFXId(TestFXId testFXId) {
        return lookupByTestFXId(testFXId.getId());
    }

    /** 
     * Looks up a node by TestFXId and returns the result of the query.
     * @pre testFXId should correspond to a valid TestFXId for a node in the scene graph. The node should be properly initialized and added to the scene graph before this method is called.
     * @param testFXId The TestFXId of the node to look up, provided as a String.
     * @return The node corresponding to the given TestFXId, cast to the appropriate type.
     * @throws ClassCastException if the node found by the TestFXId cannot be cast to the expected type.
     * @throws EmptyNodeQueryException if no node is found.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Node> T lookupByTestFXId(String testFXId) {
        return (T) new FxRobot().lookup("#" + testFXId).query();
    }

    /** Compares two Tablesaw Tables for equality by checking that they have the same number of rows and columns, the same column names in the same order, and the same values in each cell.
     * Marked as Generated since it is only used in testing.
     * 
     * @pre table1 and table2 are not null. The tables contain properly formatted data with the expected columns for the application.
     * @param table1 The first Table to be compared.
     * @param table2 The second Table to be compared.
     * @return true if the tables are equal in terms of structure and content, false otherwise.
     */
    public static boolean equals(Table table1, Table table2) {
        //Ensure same number of rows and columns
        if (table1.rowCount() != table2.rowCount() || table1.columnCount() != table2.columnCount())
            return false;
        //Ensure same column names in same order
        for( int x = 0; x < table1.columnCount(); x++ )
            if (!table1.column(x).name().equals(table2.column(x).name()))
                return false;
        //Ensure same values in each cell
        for (int r = 0; r < table1.rowCount(); r++)
            for (int c = 0; c < table1.columnCount(); c++)
                if (!table1.get(r, c).equals(table2.get(r, c)))
                    return false;

        return true;
    }
    
}