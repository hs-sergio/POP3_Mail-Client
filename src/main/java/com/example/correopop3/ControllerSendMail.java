package com.example.correopop3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class ControllerSendMail implements Initializable {

    @FXML
    private Label txtFrom;
    @FXML
    private TextField txtTo;
    @FXML
    private TextField txtSubject;
    @FXML
    private TextArea txtMensaje;

    @FXML
    private Button btnSend;
    @FXML
    private Button btnClear;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        txtFrom.setText(ControllerCorreo.loginName);

        btnClear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearAll();
            }
        });

        btnSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                String subject = txtSubject.getText();
                String To = txtTo.getText();
                String from = txtFrom.getText();
                String mensaje = txtMensaje.getText();

                if(To.isEmpty() || subject.isEmpty() || from.isEmpty() || mensaje.isEmpty()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setHeaderText("Faltan campos!");
                    alert.setContentText("Tienes que rellenar todos los campos para poder mandar un email!");
                }else{

                    String host = "smtp.gmail.com";
                    int port = 587;

                    String username = "gilnietosergio@gmail.com";
                    String password = "eflspnuitmmfeqyv";
                    Properties props = new Properties();
                    props.put("mail.smtp.host", host);
                    props.put("mail.smtp.port", port); // TLS Port
                    props.put("mail.smtp.auth", "true"); // Enable autentification
                    props.put("mail.smtp.starttls.enable", "true"); // enable StartTLS


                    Session session = Session.getDefaultInstance(props, new Authenticator() {
                        @Override
                        protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                            return new javax.mail.PasswordAuthentication(username, password);
                        }
                    });



                    try{
                        Message message = new MimeMessage(session);
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(To)); // A quien se lo vamos a enviar
                        message.setSubject(subject); // Pasamos el subject
                        message.setText(mensaje); // Pasamos el mensaje
                        System.out.println("Enviando...");
                        Transport.send(message); //
                        System.out.println("Correo electrónico enviado correctamente.");
                    }catch (MessagingException e){
                        e.printStackTrace();
                    }

                }

            }
        });

    }

    private void clearAll(){
        txtMensaje.setText("");
        txtSubject.setText("");
        txtTo.setText("");
    }

}
