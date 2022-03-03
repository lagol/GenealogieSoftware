module com.maxjacobi.genealogiesoftware {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.maxjacobi.genealogiesoftware to javafx.fxml;
    exports com.maxjacobi.genealogiesoftware;
}