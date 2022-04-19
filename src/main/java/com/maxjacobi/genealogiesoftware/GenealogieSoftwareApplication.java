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
import java.util.Optional;


public class GenealogieSoftwareApplication extends Application {
    Stage stage;

    String databaseFilePath;

    TableView personsTable = new TableView();

    Button closeDatabaseButton = new Button("Schließen");
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
        openDatabaseButton.setOnAction( ( (event) -> {
            try {
                openDatabase();
            } catch (SQLException ignored) {

            }
        }) );
        closeDatabaseButton.setDisable(true);
        closeDatabaseButton.setOnAction(((event) -> closeDatabase()));
        saveAsDatabaseButton.setDisable(true);
        newDatabaseButton.setDisable(false);
        newDatabaseButton.setOnAction(((event) -> newDatabase()));
        Button quitDatabaseButton = new Button("Beenden");
        quitDatabaseButton.setOnAction(((event) -> System.exit(69)));

        HBox databaseManagementButtons = new HBox(openDatabaseButton,closeDatabaseButton,saveAsDatabaseButton,newDatabaseButton,quitDatabaseButton);

        Label databaseName = new Label("Name:");
        renameDatabaseButton.setDisable(true);
        renameDatabaseButton.setOnAction(((event) -> renameDatabase()));

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

    private void openDatabase() throws SQLException {
        requestDatabasePath();
        closeDatabaseButton.setDisable(false);
        saveAsDatabaseButton.setDisable(false);
        renameDatabaseButton.setDisable(false);
        openDatabaseButton.setDisable(true);
        newDatabaseButton.setDisable(true);

        databaseInfoTab.setDisable(false);
        databaseStatisticsTab.setDisable(false);

        viewDatabaseInfo();
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
            databaseFilePath = file.toString();
        }
    }

    private Connection connectDatabase() throws SQLException {

        Connection c = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + databaseFilePath);
            return c;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }

        return c;

    }

    private void closeDatabase() {
        databaseNameLabel.setText("");
        databaseChangedLabel.setText("");
        databaseSizeLabel.setText("");
        databaseOwnerLabel.setText("");

        closeDatabaseButton.setDisable(true);
        saveAsDatabaseButton.setDisable(true);
        renameDatabaseButton.setDisable(true);
        openDatabaseButton.setDisable(false);
        newDatabaseButton.setDisable(false);

        databaseInfoTab.setDisable(true);
        databaseStatisticsTab.setDisable(true);

        databaseFilePath = "";
    }

    private void renameDatabase() {
        String oldName = databaseNameLabel.getText();
        TextInputDialog dialog = new TextInputDialog(oldName);
        dialog.setTitle("Datenbankname ändern");
        dialog.setHeaderText("Geben Sie einen neuen Namen ein:");
        dialog.setContentText("Name:");
        DialogPane dialogPaneCss = dialog.getDialogPane();
        dialogPaneCss.getStylesheets().add(Objects.requireNonNull(GenealogieSoftwareApplication.class.getResource("stylesheet.css")).toString());
        Optional<String> result1 = dialog.showAndWait();
        result1.ifPresent(newName -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Datenbankname ändern");
            alert.setHeaderText("Bestätigen Sie die Änderung:");
            alert.setContentText("Möchten Sie die Datenbank wirklich von '" + oldName + "' in '" + newName + "' umbenennen?");
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(Objects.requireNonNull(GenealogieSoftwareApplication.class.getResource("stylesheet.css")).toString());

            Optional<ButtonType> result2 = alert.showAndWait();
            if (result2.isPresent()) {
                if (result2.get() == ButtonType.OK) {
                    try {
                        Connection c = connectDatabase();
                        Statement statement = c.createStatement();
                        String sql = "UPDATE GENERAL SET NAME='" + newName + "'";
                        statement.executeUpdate(sql);
                        viewDatabaseInfo();
                        c.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void viewDatabaseInfo() throws SQLException {
        Connection c = connectDatabase();
        String name = "",changed = "",size = "",owner = "";
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


        databaseNameLabel.setText(name);
        databaseChangedLabel.setText(changed);
        databaseSizeLabel.setText(size);
        databaseOwnerLabel.setText(owner);

    }

}