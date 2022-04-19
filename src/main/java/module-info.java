module com.maxjacobi.genealogiesoftware {
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires javafx.graphics;
    requires javafx.controls;

    opens com.maxjacobi.genealogiesoftware to javafx.fxml;
    exports com.maxjacobi.genealogiesoftware;
}