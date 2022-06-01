module uw.edu.css553 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;

    opens uw.edu.css553 to javafx.fxml;
    exports uw.edu.css553.appender;
    opens uw.edu.css553.appender to javafx.fxml;
    exports uw.edu.css553.report;
    opens uw.edu.css553.report to javafx.fxml;
}