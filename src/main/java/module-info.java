module com.jhonas.pageturner2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens com.jhonas.pageturner2 to javafx.fxml;
    opens com.jhonas.pageturner2.ui to javafx.fxml;
    opens com.jhonas.pageturner2.model to com.fasterxml.jackson.databind, javafx.base;

    exports com.jhonas.pageturner2;
}