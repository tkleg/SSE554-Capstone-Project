package org.troy.capstone.basictests;

import org.troy.capstone.utils.TableUtils;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tech.tablesaw.api.Table;

public class JavaFXTest extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        TableView<Animal> table = new TableView<>(FXCollections.observableArrayList(
            new Animal("bear", 90.1),
            new Animal("cat", 84.3),
            new Animal("giraffe", 99.7)
        ));
        
        TableColumn<Animal, String> nameCol = new TableColumn<>("Animal Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Animal, Double> weightCol = new TableColumn<>("Cuteness Rating");
        weightCol.setCellValueFactory(new PropertyValueFactory<>("weight"));
        
        table.getColumns().addAll(nameCol, weightCol);
        
        // Lock table to fixed size with scrolling
        table.setPrefWidth(300);
        table.setPrefHeight(200);
        table.setMaxWidth(300);
        table.setMaxHeight(200);
        
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(table);
        
        Button button = new Button("Add new table");
        button.setOnAction(e ->{
            TableView<ObservableList<Object>> nflTable = TableUtils.tablesawTableToTableView( 
                Table.read().csv("data/teams.csv")
            );
            // Lock NFL table to fixed size with scrolling
            nflTable.setPrefWidth(350);
            nflTable.setPrefHeight(250);
            nflTable.setMaxWidth(350);
            nflTable.setMaxHeight(250);
            borderPane.setRight(nflTable);
        });
        borderPane.setBottom(button);
        
        primaryStage.setScene(new Scene(borderPane, 400, 300));
        primaryStage.setTitle("JavaFX Test - Cute Animals");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
