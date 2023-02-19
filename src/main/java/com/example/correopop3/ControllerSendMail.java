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
                    String username = "pruebasmtpcdm@gmail.com";
                    String password = "pruebasmtp";
                    char[] passwordChar = password.toCharArray();

                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", host);
                    props.put("mail.smtp.port", port);

                    Session session = Session.getInstance(props, new Authenticator() {
                        protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                            return new javax.mail.PasswordAuthentication(username, password);
                        }
                    });

                    try{
                        Message message = new MimeMessage(session);
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(To));
                        message.setSubject(subject);
                        message.setText(mensaje);
                        Transport.send(message);
                        System.out.println("Correo electrónico enviado correctamente.");
                    }catch (MessagingException e){
                        e.printStackTrace();
                    }

                }

            }
        });

    }
}
