package com.maxjacobi.genealogiesoftware;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class GenealogieSoftwareController {

    @FXML
    private TableView personsTable;
    @FXML
    private Label databaseSizeLabel;
    @FXML
    private Label databaseOwnerLabel;
    @FXML
    private Label databaseNameLabel;
    @FXML
    private Label databaseChangedLabel;

    @FXML
    private void initialize() {
        databaseNameLabel.setText("");
        databaseChangedLabel.setText("");
        databaseSizeLabel.setText("");
        databaseOwnerLabel.setText("");

        personsTable.setEditable(false);

        TableColumn nameColumn = new TableColumn("Name");
        TableColumn sexColumn = new TableColumn("Geschlecht");
        TableColumn idColumn = new TableColumn("ID");
        TableColumn birthDateColumn = new TableColumn("Geburtsdatum");
        TableColumn deathDateColumn = new TableColumn("Sterbedatum");

        personsTable.getColumns().addAll(nameColumn,sexColumn,idColumn,birthDateColumn,deathDateColumn);

        final ObservableList<personsTablePerson> data = FXCollections.observableArrayList(
               new personsTablePerson("Max Jacobi","m","1","2006-03-16",""),
               new personsTablePerson("Leo Jacobi","m","2","2007-11-15","")
        );

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<personsTablePerson,String>("name")
        );
        sexColumn.setCellValueFactory(
                new PropertyValueFactory<personsTablePerson,String>("sex")
        );
        idColumn.setCellValueFactory(
                new PropertyValueFactory<personsTablePerson,String>("id")
        );
        birthDateColumn.setCellValueFactory(
                new PropertyValueFactory<personsTablePerson,String>("birthDate")
        );
        deathDateColumn.setCellValueFactory(
                new PropertyValueFactory<personsTablePerson,String>("deathDate")
        );

        personsTable.setItems(data);

    }
    @FXML
    private void quitApplication() {
        Platform.exit();
    }
}