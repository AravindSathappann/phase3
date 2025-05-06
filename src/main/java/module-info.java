module com.example.phase3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.github.cdimascio.dotenv.java;
    requires java.sql;


    opens com.example.phase3 to javafx.fxml;
    exports com.example.phase3;
}