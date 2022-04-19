package com.maxjacobi.genealogiesoftware;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.util.Objects;


public class GenealogieSoftwareApplication extends Application {
    Stage stage;

    Connection c = null;

    TableView personsTable = new TableView();

    Button closeDatabaseButton = new Button("Schließen");
    Button saveDatabaseButton = new Button("Speichern");
    Button saveAsDatabaseButton = new Button("Speichern unter");
    Button openDatabaseButton = new Button("Öffnen");
    Button newDatabaseButton = new Button("Neu");
    Button renameDatabaseButton = new Button("Umbenennen");

    Tab databaseStatisticsTab = new Tab("Statistiken");
    Tab databaseInfoTab = new Tab("Datenbank");

    Label databaseNameLabel = new Label();
    Label databaseChangedLabel = new Label();
    Label databaseSizeLabel = new Label();
    Label databaseOwnerLabel = new Label();

    @Override
    public void start(Stage stage) {

        openDatabaseButton.setDisable(false);
        openDatabaseButton.setOnAction( ( (event) -> requestDatabasePath() ) );
        closeDatabaseButton.setDisable(true);
        saveDatabaseButton.setDisable(true);
        saveAsDatabaseButton.setDisable(true);
        newDatabaseButton.setDisable(false);
        newDatabaseButton.setOnAction(((event) -> newDatabase()));
        Button quitDatabaseButton = new Button("Beenden");
        quitDatabaseButton.setOnAction(((event) -> System.exit(69)));

        HBox databaseManagementButtons = new HBox(openDatabaseButton,closeDatabaseButton,saveDatabaseButton,saveAsDatabaseButton,newDatabaseButton,quitDatabaseButton);

        Label databaseName = new Label("Name:");
        renameDatabaseButton.setDisable(true);

        HBox databaseInfoNameHBox = new HBox(databaseName,databaseNameLabel,renameDatabaseButton);

        Label databaseChanged = new Label("Änderungsdatum:");

        HBox databaseInfoChanged = new HBox(databaseChanged,databaseChangedLabel);

        Label databaseSize = new Label("Größe:");

        HBox databaseInfoSize = new HBox(databaseSize,databaseSizeLabel);

        Label databaseOwner = new Label("Besitzer:");

        HBox databaseInfoOwner = new HBox(databaseOwner,databaseOwnerLabel);

        VBox databaseInfoVBox = new VBox(databaseInfoNameHBox,databaseInfoChanged,databaseInfoSize,databaseInfoOwner);

        databaseInfoTab.setContent(databaseInfoVBox);
        databaseInfoTab.setDisable(true);
        databaseStatisticsTab.setDisable(true);

        TabPane databaseInfoTabPane = new TabPane(databaseInfoTab,databaseStatisticsTab);
        databaseInfoTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox rootStartVBox = new VBox(databaseManagementButtons,databaseInfoTabPane);

        Tab rootStartTab = new Tab("Start",rootStartVBox);

        personsTable.setPrefHeight(600.0);

        VBox rootPersonsVBox = new VBox(personsTable);

        Tab rootPersonsTab = new Tab("Personen",rootPersonsVBox);

        Tab rootRelationsTab = new Tab("Beziehungen");

        Tab rootFamiliesTab = new Tab("Familien");

        Tab rootSourcesTab = new Tab("Quellen");

        Tab rootMediaTab = new Tab("Medien");

        TabPane rootTabPane = new TabPane(rootStartTab,rootPersonsTab,rootRelationsTab,rootFamiliesTab,rootSourcesTab,rootMediaTab);
        rootTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Label leftInfoLabel = new Label();
        Label rightInfoLabel = new Label("Genealogie Software 1.0.0");

        BorderPane rootFooterPane = new BorderPane();
        rootFooterPane.setPrefWidth(600.0);
        rootFooterPane.setPrefHeight(13.0);
        rootFooterPane.setLeft(leftInfoLabel);
        rootFooterPane.setRight(rightInfoLabel);

        BorderPane rootContainer = new BorderPane();
        rootContainer.setPrefHeight(600.0);
        rootContainer.setPrefWidth(800.0);
        rootContainer.setCenter(rootTabPane);
        rootContainer.setBottom(rootFooterPane);

        Scene scene = new Scene(rootContainer, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(GenealogieSoftwareApplication.class.getResource("stylesheet.css")).toString());
        stage.setTitle("Genealogie Software");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }

    private void newDatabase() {

    }

    private void requestDatabasePath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Datenbank Datei auswählen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Alle Dateien", "*.*"),
                new FileChooser.ExtensionFilter("SQLite Datenbank", "*.db")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                connectDatabase(file.toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectDatabase(String path) throws SQLException {

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        closeDatabaseButton.setDisable(false);
        saveDatabaseButton.setDisable(false);
        saveAsDatabaseButton.setDisable(false);
        renameDatabaseButton.setDisable(false);
        openDatabaseButton.setDisable(true);
        newDatabaseButton.setDisable(true);

        databaseInfoTab.setDisable(false);
        databaseStatisticsTab.setDisable(false);

        viewDatabaseInfo();


    }

    private void viewDatabaseInfo() {
        final Connection connection = c;
        String name = "",changed = "",size = "",owner = "";
        try(connection) {
            try(Statement statement = c.createStatement()){
                String sql = "SELECT * FROM GENERAL";
                try(ResultSet result = statement.executeQuery(sql)){
                    while(result.next()) {
                        name = result.getString("NAME");
                        changed = result.getString("CHANGEDATE");
                        size = result.getString("SIZE");
                        owner = result.getString("OWNER");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        databaseNameLabel.setText(name);
        databaseChangedLabel.setText(changed);
        databaseSizeLabel.setText(size);
        databaseOwnerLabel.setText(owner);

    }

}