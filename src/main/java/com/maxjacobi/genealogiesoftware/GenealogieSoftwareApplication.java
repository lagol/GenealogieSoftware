package com.maxjacobi.genealogiesoftware;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.math3.util.Precision;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;


public class GenealogieSoftwareApplication extends Application {
    Stage stage;

    String databaseFilePath;

    TableView<personsTablePerson> personsTable = new TableView<>();

    Button closeDatabaseButton = new Button("Schließen");
    Button saveAsDatabaseButton = new Button("Speichern unter");
    Button openDatabaseButton = new Button("Öffnen");
    Button newDatabaseButton = new Button("Neu");
    Button renameDatabaseButton = new Button("Umbenennen");
    Button changeOwnerButton = new Button("Ändern");

    Tab databaseStatisticsTab = new Tab("Statistiken");
    Tab databaseInfoTab = new Tab("Datenbank");

    Label databaseNameLabel = new Label();
    Label databaseChangedLabel = new Label();
    Label databaseSizeLabel = new Label();
    Label databaseOwnerLabel = new Label();

    Label databaseNumberOfPersonsLabel = new Label();
    Label databaseNumberOfMalePersonsLabel = new Label();
    Label databaseNumberOfFemalePersonsLabel = new Label();
    Label databaseNumberOfFamiliesLabel = new Label();

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

        Label databaseOwner = new Label("Besitzer:in:");
        changeOwnerButton.setDisable(true);
        changeOwnerButton.setOnAction(((event) -> changeDatabaseOwnerInfo()));

        HBox databaseInfoOwner = new HBox(databaseOwner,databaseOwnerLabel,changeOwnerButton);

        VBox databaseInfoVBox = new VBox(databaseInfoNameHBox,databaseInfoChanged,databaseInfoSize,databaseInfoOwner);

        Label databaseNumberOfPersons = new Label("Anzahl Personen:");

        HBox databaseStatisticsNumberOfPersons = new HBox(databaseNumberOfPersons,databaseNumberOfPersonsLabel);

        Label databaseNumberOfMalePersons = new Label("davon männlich:");

        HBox databaseStatisticsNumberOfMalePersons = new HBox(databaseNumberOfMalePersons,databaseNumberOfMalePersonsLabel);

        Label databaseNumberOfFemalePersons = new Label("davon weiblich:");

        HBox databaseStatisticsNumberOfFemalePersons = new HBox(databaseNumberOfFemalePersons,databaseNumberOfFemalePersonsLabel);

        Label databaseNumberOfFamilies = new Label("Anzahl Familien:");

        HBox databaseStatisticsNumberOfFamilies = new HBox(databaseNumberOfFamilies,databaseNumberOfFamiliesLabel);

        VBox databaseStatisticsVBox = new VBox(databaseStatisticsNumberOfPersons,databaseStatisticsNumberOfMalePersons,databaseStatisticsNumberOfFemalePersons,databaseStatisticsNumberOfFamilies);

        databaseInfoTab.setContent(databaseInfoVBox);
        databaseInfoTab.setDisable(true);
        databaseStatisticsTab.setContent(databaseStatisticsVBox);
        databaseStatisticsTab.setDisable(true);

        TabPane databaseInfoTabPane = new TabPane(databaseInfoTab,databaseStatisticsTab);
        databaseInfoTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox rootStartVBox = new VBox(databaseManagementButtons,databaseInfoTabPane);

        Tab rootStartTab = new Tab("Start",rootStartVBox);

        TableColumn<personsTablePerson,String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(250);
        TableColumn<personsTablePerson,String> sexColumn = new TableColumn<>("Geschlecht");
        sexColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
        sexColumn.setPrefWidth(75);
        TableColumn<personsTablePerson,String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(75);
        TableColumn<personsTablePerson,String> birthColumn = new TableColumn<>("Geburt");
        birthColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        birthColumn.setPrefWidth(200.0);
        TableColumn<personsTablePerson,String> deathColumn = new TableColumn<>("Tod");
        deathColumn.setCellValueFactory(new PropertyValueFactory<>("deathDate"));
        deathColumn.setPrefWidth(200.0);

        Button personsAddPerson = new Button("Hinzufügen");
        personsAddPerson.setOnAction(((event) -> {
            try {
                editPerson("new");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
        Button personsRemovePerson = new Button("Entfernen");
        personsRemovePerson.setOnAction(((event) -> removePerson()));
        Button personsEditPerson = new Button("Bearbeiten");
        personsEditPerson.setOnAction(((event) -> {
            try {
                editPerson("edit");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
        ToolBar personsToolbar = new ToolBar(personsAddPerson,personsRemovePerson,personsEditPerson);

        personsTable.getColumns().add(nameColumn);
        personsTable.getColumns().add(sexColumn);
        personsTable.getColumns().add(idColumn);
        personsTable.getColumns().add(birthColumn);
        personsTable.getColumns().add(deathColumn);
        personsTable.setEditable(false);
        personsTable.setDisable(true);
        personsTable.setPrefHeight(600.0);

        VBox rootPersonsVBox = new VBox(personsToolbar,personsTable);

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
        changeOwnerButton.setDisable(false);
        openDatabaseButton.setDisable(true);
        newDatabaseButton.setDisable(true);

        databaseInfoTab.setDisable(false);
        databaseStatisticsTab.setDisable(false);

        personsTable.setDisable(false);

        writeDatabaseInfo();
        viewDatabaseStatistics();
        viewPersonsTable();
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

        databaseNumberOfPersonsLabel.setText("");
        databaseNumberOfMalePersonsLabel.setText("");
        databaseNumberOfFemalePersonsLabel.setText("");
        databaseNumberOfFamiliesLabel.setText("");

        closeDatabaseButton.setDisable(true);
        saveAsDatabaseButton.setDisable(true);
        renameDatabaseButton.setDisable(true);
        changeOwnerButton.setDisable(true);
        openDatabaseButton.setDisable(false);
        newDatabaseButton.setDisable(false);

        databaseInfoTab.setDisable(true);
        databaseStatisticsTab.setDisable(true);

        personsTable.setDisable(true);

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
                        c.close();
                        writeDatabaseInfo();
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

        c.close();

        databaseNameLabel.setText(name);
        databaseChangedLabel.setText(changed);
        databaseSizeLabel.setText(size + " Bytes");
        databaseOwnerLabel.setText(owner);

    }

    private void viewDatabaseStatistics() throws SQLException {
        Connection c = connectDatabase();
        int personsCounter = 0, malesCounter = 0, femalesCounter = 0;
        try(Statement statement = c.createStatement()) {
            String sql = "SELECT * FROM PERSON";
            try(ResultSet result = statement.executeQuery(sql)) {
                while(result.next()) {
                    personsCounter++;
                    if (result.getString("SEX").equals("M")) {
                        malesCounter++;
                    } else if (result.getString("SEX").equals("F")) {
                        femalesCounter++;
                    }
                }
            }
        }
        double malePercentage = 0.0,femalePercentage = 0.0;
        if (personsCounter != 0) {
            if (malesCounter != 0) {
                malePercentage = Precision.round((double) malesCounter / (double) personsCounter * 100, 1);
            }
            if (femalesCounter != 0) {
                femalePercentage = Precision.round((double) femalesCounter / (double) personsCounter * 100, 1);
            }
        }
        databaseNumberOfPersonsLabel.setText(Integer.toString(personsCounter));
        databaseNumberOfMalePersonsLabel.setText(malesCounter + " (" + malePercentage + "%)");
        databaseNumberOfFemalePersonsLabel.setText(femalesCounter + " (" + femalePercentage + "%)");

        int familiesCounter = 0;
        try(Statement statement = c.createStatement()) {
            String sql = "SELECT * FROM FAMILY";
            try (ResultSet result = statement.executeQuery(sql)) {
                while (result.next()) {
                    familiesCounter++;
                }
            }
        }
        databaseNumberOfFamiliesLabel.setText(Integer.toString(familiesCounter));

        c.close();

    }

    private void writeDatabaseInfo() throws SQLException {
        Connection c = connectDatabase();
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        String formattedDateTime = dateTime.format(formatter);
        File file = new File(databaseFilePath);
        String fileSize = Long.toString(file.length());
        try(Statement statement = c.createStatement()) {
            String sql = "UPDATE GENERAL SET CHANGEDATE='" + formattedDateTime + "'";
            statement.executeUpdate(sql);
            sql = "UPDATE GENERAL SET SIZE='" + fileSize + "'";
            statement.executeUpdate(sql);
            viewDatabaseInfo();
            c.close();
        }
    }

    private void changeDatabaseOwnerInfo() {
        String oldOwner = databaseOwnerLabel.getText();
        TextInputDialog dialog = new TextInputDialog(oldOwner);
        dialog.setTitle("Datenbankbesitzer:in ändern");
        dialog.setHeaderText("Geben Sie neue Informationen zum:zur Besitzer:in ein:");
        dialog.setContentText("Besitzer:in");
        DialogPane dialogPaneCss = dialog.getDialogPane();
        dialogPaneCss.getStylesheets().add(Objects.requireNonNull(GenealogieSoftwareApplication.class.getResource("stylesheet.css")).toString());
        Optional<String> result1 = dialog.showAndWait();
        result1.ifPresent(newOwner -> {
           Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
           alert.setTitle("Datebankbesitzer:in ändern");
           alert.setHeaderText("Bestätigen Sie die Änderung:");
           alert.setContentText("Möchten Sie die wirklich die Informationen zum:zur Besitzer:in der Datenbank von '" + oldOwner + "' in '" + newOwner + "' ändern?");
           DialogPane dialogPane = alert.getDialogPane();
           dialogPane.getStylesheets().add(Objects.requireNonNull(GenealogieSoftwareApplication.class.getResource("stylesheet.css")).toString());
           Optional<ButtonType> result2 = alert.showAndWait();
           if (result2.isPresent()) {
               if (result2.get() == ButtonType.OK) {
                   try {
                       Connection c = connectDatabase();
                       Statement statement = c.createStatement();
                       String sql = "UPDATE GENERAL SET OWNER='" + newOwner + "'";
                       statement.executeUpdate(sql);
                       c.close();
                       writeDatabaseInfo();
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }
           }
        });
    }

    private void viewPersonsTable() throws SQLException {
        ObservableList<personsTablePerson> data = FXCollections.observableArrayList();
        Connection c = connectDatabase();
        try(Statement statement = c.createStatement()) {
            String sql = "SELECT NAME.GIVENNAMES,NAME.LASTNAME,PERSON.SEX,PERSON.ID,EVENT.DATE FROM PERSON,NAME,EVENT WHERE PERSON.ID = NAME.PID AND PERSON.ID = EVENT.PID AND EVENT.TYPE='birth' GROUP BY PERSON.ID";
            try(ResultSet result = statement.executeQuery(sql)) {
                while(result.next()) {
                    String id = result.getString("ID");
                    String name = result.getString("GIVENNAMES") + " " + result.getString("LASTNAME");
                    String sex = result.getString("SEX");
                    String birth = result.getString("DATE");
                    String death = "";
                    Statement deadStatement = c.createStatement();
                    String deadSql = "SELECT DATE FROM EVENT WHERE TYPE='death' AND PID='" + id + "' LIMIT 1";
                    ResultSet deadResult = deadStatement.executeQuery(deadSql);
                    while(deadResult.next()) {
                        death = deadResult.getString("DATE");
                    }
                    data.add(new personsTablePerson(name,sex,id,birth,death));
                }
            }
        }
        personsTable.setItems(data);
        c.close();
    }

    private void editPerson(String how) throws SQLException {

        Button editPersonConfirm = new Button("Okay");
        Button editPersonCancel = new Button("Abbrechen");
        ToolBar editPersonConfirmButtons = new ToolBar(editPersonCancel,editPersonConfirm);

        BorderPane editPersonConfirmBorderPane = new BorderPane();
        editPersonConfirmBorderPane.setPrefHeight(13.0);
        editPersonConfirmBorderPane.setRight(editPersonConfirmButtons);

        Label editSex = new Label("Geschlecht:");
        editSex.setPrefWidth(100.0);
        RadioButton maleSexButton = new RadioButton("Männlich");
        maleSexButton.setPrefWidth(175.0);
        RadioButton femaleSexButton = new RadioButton("Weiblich");
        femaleSexButton.setPrefWidth(175.0);
        RadioButton otherSexButton = new RadioButton("Anderes:");
        otherSexButton.setPrefWidth(175.0);
        ToggleGroup sexSelector = new ToggleGroup();
        sexSelector.getToggles().addAll(maleSexButton,femaleSexButton,otherSexButton);
        TextField editSexOtherField = new TextField();
        editSexOtherField.setPrefWidth(175.0);
        HBox editPersonSex = new HBox(editSex,maleSexButton,femaleSexButton,otherSexButton,editSexOtherField);

        Label editNameType = new Label("Typ:");
        editNameType.setPrefWidth(100.0);
        ComboBox<String> nameTypeSelector = new ComboBox<>();
        nameTypeSelector.getItems().addAll("","Geburtsname","Adoptivname","Ehename","Taufname");
        nameTypeSelector.setPrefWidth(700.0);
        HBox editPersonNameType = new HBox(editNameType,nameTypeSelector);

        Label editNameTitle = new Label("Titel:");
        editNameTitle.setPrefWidth(100.0);
        TextField editNameTitleField = new TextField();
        editNameTitleField.setPrefWidth(700.0);
        HBox editPersonNameTitle = new HBox(editNameTitle,editNameTitleField);

        Label editNameGivenNames = new Label("Vorname(n):");
        editNameGivenNames.setPrefWidth(100.0);
        TextField editNameGivenNamesField = new TextField();
        editNameGivenNamesField.setPrefWidth(700.0);
        HBox editPersonNameGivenNames = new HBox(editNameGivenNames,editNameGivenNamesField);

        Label editNameCallName = new Label("Rufname:");
        editNameCallName.setPrefWidth(100.0);
        TextField editNameCallNameField = new TextField();
        editNameCallNameField.setPrefWidth(700.0);
        HBox editPersonNameCallName = new HBox(editNameCallName,editNameCallNameField);

        Label editNameNickName = new Label("Spitzname:");
        editNameNickName.setPrefWidth(100.0);
        TextField editNameNickNameField = new TextField();
        editNameNickNameField.setPrefWidth(700.0);
        HBox editPersonNameNickName = new HBox(editNameNickName,editNameNickNameField);

        Label editNamePrefix = new Label("Präfix:");
        editNamePrefix.setPrefWidth(100.0);
        TextField editNamePrefixField = new TextField();
        editNamePrefixField.setPrefWidth(700.0);
        HBox editPersonNamePrefix = new HBox(editNamePrefix,editNamePrefixField);

        Label editNameLastName = new Label("Nachname:");
        editNameLastName.setPrefWidth(100.0);
        TextField editNameLastNameField = new TextField();
        editNameLastNameField.setPrefWidth(700.0);
        HBox editPersonNameLastName = new HBox(editNameLastName,editNameLastNameField);

        Label editNameSuffix = new Label("Suffix:");
        editNameSuffix.setPrefWidth(100.0);
        TextField editNameSuffixField = new TextField();
        editNameSuffixField.setPrefWidth(700.0);
        HBox editPersonNameSuffix = new HBox(editNameSuffix,editNameSuffixField);

        Label editNameOrigin = new Label("Herkunft:");
        editNameOrigin.setPrefWidth(100.0);
        TextField editNameOriginField = new TextField();
        editNameOriginField.setPrefWidth(700.0);
        HBox editPersonNameOrigin = new HBox(editNameOrigin,editNameOriginField);

        Tab editPersonEvents = new Tab("Ereignisse");
        Tab editPersonNames = new Tab("Namen");
        Tab editPersonSources = new Tab("Quellen");
        Tab editPersonMedia = new Tab("Medien");
        TabPane editPersonTabPane = new TabPane(editPersonEvents,editPersonNames,editPersonSources,editPersonMedia);
        editPersonTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox editPersonVBox = new VBox(editPersonSex,editPersonNameType,editPersonNameTitle,editPersonNameGivenNames,editPersonNameCallName,editPersonNameNickName,editPersonNamePrefix,editPersonNameLastName,editPersonNameSuffix,editPersonNameOrigin,editPersonTabPane);
        editPersonVBox.setPrefHeight(587.0);

        BorderPane editPersonBorderPane = new BorderPane();
        editPersonBorderPane.setPrefHeight(600.0);
        editPersonBorderPane.setCenter(editPersonVBox);
        editPersonBorderPane.setBottom(editPersonConfirmBorderPane);

        Scene editPersonScene = new Scene(editPersonBorderPane,800,600);
        editPersonScene.getStylesheets().add(Objects.requireNonNull(GenealogieSoftwareApplication.class.getResource("stylesheet.css")).toString());
        Stage editPersonStage = new Stage();
        if (how.equals("new")) {
            editPersonStage.setTitle("Person hinzufügen");
        } else {
            editPersonStage.setTitle("Person bearbeiten");
        }
        editPersonStage.setScene(editPersonScene);
        editPersonStage.show();
        editPersonCancel.setOnAction(((event) -> editPersonStage.close()));
        editPersonConfirm.setOnAction(((event) -> {
            if (how.equals("edit")) {
                String personId = personsTable.getItems().get(personsTable.getSelectionModel().getSelectedIndex()).getId();
                String sex;
                if (maleSexButton.isSelected()) {
                    sex = "M";
                } else if (femaleSexButton.isSelected()) {
                    sex = "F";
                } else {
                    sex = editSexOtherField.getText();
                }
                LocalDateTime dateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
                String formattedDateTime = dateTime.format(formatter);
                String nameType = "";
                if (nameTypeSelector.getSelectionModel().getSelectedItem() != null) {
                    switch (nameTypeSelector.getSelectionModel().getSelectedItem()) {
                        case "Geburtsname" -> nameType = "birth";
                        case "Ehename" -> nameType = "marriage";
                        case "Adoptivname" -> nameType = "adoption";
                        default -> nameType = "";
                    }
                }
                try {
                    Connection c = connectDatabase();
                    Statement statement = c.createStatement();
                    String sql = "UPDATE PERSON SET SEX='" + sex + "',CHANGEDATE='" + formattedDateTime + "' WHERE ID='" + personId + "'";
                    statement.executeUpdate(sql);
                    sql = "SELECT ID FROM NAME WHERE PID='" + personId + "'";
                    ResultSet result = statement.executeQuery(sql);
                    String nameId = result.getString("ID");
                    sql = "UPDATE NAME SET LASTNAME='" + editNameLastNameField.getText() + "',PREFIX='" + editNamePrefixField.getText() + "',GIVENNAMES='" + editNameGivenNamesField.getText() + "',CALLNAME='" + editNameCallNameField.getText() + "',NICKNAME='" + editNameNickNameField.getText() + "',SUFFIX='" + editNameSuffixField.getText() + "',TITLE='" + editNameTitleField.getText() + "',ORIGIN='" + editNameOriginField.getText() + "',TYPE='" + nameType + "',CHANGEDATE='" + formattedDateTime + "' WHERE ID='" + nameId + "'";
                    statement.executeUpdate(sql);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (how.equals("new")) {
                String personId = "";
                try {
                    Connection c = connectDatabase();
                    Statement statement = c.createStatement();
                    String sql = "SELECT * FROM PERSON";
                    ResultSet result = statement.executeQuery(sql);
                    int personsCounter = 0;
                    while (result.next()) {
                        personsCounter++;
                    }
                    while (statement.executeQuery("SELECT * FROM PERSON WHERE ID='P" + personsCounter + "'").next()) {
                        personsCounter++;
                    }
                    personId = "P" + personsCounter;
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                String sex;
                if (maleSexButton.isSelected()) {
                    sex = "M";
                } else if (femaleSexButton.isSelected()) {
                    sex = "F";
                } else {
                    sex = editSexOtherField.getText();
                }
                LocalDateTime dateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
                String formattedDateTime = dateTime.format(formatter);
                String nameType = "";
                if (nameTypeSelector.getSelectionModel().getSelectedItem() != null) {
                    switch (nameTypeSelector.getSelectionModel().getSelectedItem()) {
                        case "Geburtsname" -> nameType = "birth";
                        case "Ehename" -> nameType = "marriage";
                        case "Adoptivname" -> nameType = "adoption";
                        default -> nameType = "";
                    }
                }
                try {
                    Connection c = connectDatabase();
                    Statement statement = c.createStatement();
                    String sql = "INSERT INTO PERSON(ID,SEX,CHANGEDATE) VALUES('" + personId + "','" + sex + "','" + formattedDateTime + "')";
                    statement.executeUpdate(sql);
                    String nameId = "";
                    try {
                        sql = "SELECT * FROM NAME";
                        ResultSet result = statement.executeQuery(sql);
                        int namesCounter = 0;
                        while (result.next()) {
                            namesCounter++;
                        }
                        while (statement.executeQuery("SELECT * FROM NAME WHERE ID='X" + namesCounter + "'").next()) {
                            namesCounter++;
                        }
                        nameId = "X" + namesCounter;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    String lastName = "",prefix="",givenNames="",callName="",nickName="",suffix="",title="",origin="";
                    if (editNameLastNameField.getText() != null)
                        lastName = editNameLastNameField.getText();
                    if (editNamePrefixField.getText() != null)
                        prefix = editNamePrefixField.getText();
                    if (editNameGivenNamesField.getText() != null)
                        givenNames = editNameGivenNamesField.getText();
                    if (editNameCallNameField.getText() != null)
                        callName = editNameCallNameField.getText();
                    if (editNameNickNameField.getText() != null)
                        nickName = editNameNickNameField.getText();
                    if (editNameSuffixField.getText() != null)
                        suffix = editNameSuffixField.getText();
                    if (editNameTitleField.getText() != null)
                        title = editNameTitleField.getText();
                    if (editNameOriginField.getText() != null)
                        origin = editNameOriginField.getText();
                    sql = "INSERT INTO NAME(ID,LASTNAME,PREFIX,GIVENNAMES,CALLNAME,NICKNAME,SUFFIX,TITLE,ORIGIN,TYPE,CHANGEDATE,PID) VALUES('" + nameId + "','" + lastName + "','" + prefix + "','" + givenNames + "','" + callName + "','" + nickName + "','" + suffix + "','" + title + "','" + origin + "','" + nameType + "','" + formattedDateTime + "','" + personId + "')";
                    statement.executeUpdate(sql);
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                writeDatabaseInfo();
                viewDatabaseStatistics();
                viewPersonsTable();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            editPersonStage.close();
        }));

        if (how.equals("edit")) {
            String personId = personsTable.getItems().get(personsTable.getSelectionModel().getSelectedIndex()).getId();
            Connection c = connectDatabase();
            try(Statement statement = c.createStatement()) {
                String sql = "SELECT TITLE,GIVENNAMES,CALLNAME,NICKNAME,PREFIX,LASTNAME,SUFFIX,ORIGIN,TYPE FROM NAME WHERE PID='" + personId + "' LIMIT 1";
                try(ResultSet result = statement.executeQuery(sql)) {
                    while(result.next()) {
                        if(result.getString("TITLE") != null)
                            editNameTitleField.setText(result.getString("TITLE"));
                        if(result.getString("GIVENNAMES") != null)
                        editNameGivenNamesField.setText(result.getString("GIVENNAMES"));
                        if(result.getString("CALLNAME") != null)
                        editNameCallNameField.setText(result.getString("CALLNAME"));
                        if(result.getString("NICKNAME") != null)
                        editNameNickNameField.setText(result.getString("NICKNAME"));
                        if(result.getString("PREFIX") != null)
                        editNamePrefixField.setText(result.getString("PREFIX"));
                        if(result.getString("LASTNAME") != null)
                        editNameLastNameField.setText(result.getString("LASTNAME"));
                        if(result.getString("SUFFIX") != null)
                        editNameSuffixField.setText(result.getString("SUFFIX"));
                        if(result.getString("ORIGIN") != null)
                        editNameOriginField.setText(result.getString("ORIGIN"));
                        if (result.getString("TYPE") != null) {
                            switch (result.getString("TYPE")) {
                                case "birth" -> nameTypeSelector.getSelectionModel().select("Geburtsname");
                                case "marriage" -> nameTypeSelector.getSelectionModel().select("Ehename");
                                case "adoption" -> nameTypeSelector.getSelectionModel().select("Adoptivname");
                                default -> nameTypeSelector.getSelectionModel().selectFirst();
                            }
                        }
                    }
                }
            }
            try(Statement statement = c.createStatement()) {
                String sql = "SELECT SEX FROM PERSON WHERE ID='" + personId + "'";
                try(ResultSet result = statement.executeQuery(sql)) {
                    while (result.next()) {
                        switch (result.getString("SEX")) {
                            case "M" -> maleSexButton.setSelected(true);
                            case "F" -> femaleSexButton.setSelected(true);
                            default -> {
                                otherSexButton.setSelected(true);
                                editSexOtherField.setText(result.getString("SEX"));
                            }
                        }
                    }
                }
            }
            c.close();
        }

    }

    private void removePerson() {

    }

}