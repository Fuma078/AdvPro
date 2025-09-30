module se233.chapter6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;

    opens se233.chapter6 to javafx.fxml;
    exports se233.chapter6;
    exports se233.chapter6.model;
    exports se233.chapter6.view;
    exports se233.chapter6.controller;
}