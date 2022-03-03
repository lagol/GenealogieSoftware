package com.maxjacobi.genealogiesoftware;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GenealogieSoftwareController {

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
    }
    @FXML
    private void quitApplication() {
        Platform.exit();
    }
}