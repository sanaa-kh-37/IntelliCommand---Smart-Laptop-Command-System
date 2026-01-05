module com.example.commandinterpreter {
    requires javafx.controls;
    requires javafx.fxml;

    // Add this line to fix your error
    requires java.sql;

    // If you're using MySQL driver (non-modular), you might need this too
    requires mysql.connector.j;

    opens com.example.commandinterpreter to javafx.fxml;
    opens com.example.commandinterpreter.controller to javafx.fxml;
    opens com.example.commandinterpreter.model to javafx.fxml;
    opens com.example.commandinterpreter.db to javafx.fxml;

    exports com.example.commandinterpreter;
    exports com.example.commandinterpreter.controller;
    exports com.example.commandinterpreter.db;
    exports com.example.commandinterpreter.model;
}