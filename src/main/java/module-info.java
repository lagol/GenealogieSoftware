module com.maxjacobi.genealogiesoftware {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;


    opens com.maxjacobi.genealogiesoftware to javafx.fxml;
    exports com.maxjacobi.genealogiesoftware;
}