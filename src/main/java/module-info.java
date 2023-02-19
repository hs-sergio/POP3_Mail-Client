module com.example.correopop3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.net;
    requires java.mail;


    opens com.example.correopop3 to javafx.fxml;
    exports com.example.correopop3;
}