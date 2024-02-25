module lab01 {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;
    opens lab to javafx.fxml;
    exports lab;
}