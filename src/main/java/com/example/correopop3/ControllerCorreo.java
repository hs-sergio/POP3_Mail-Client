package com.example.correopop3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.net.pop3.POP3Client;
import org.apache.commons.net.pop3.POP3MessageInfo;

public class ControllerCorreo implements Initializable {

    @FXML
    private TableView<MensajeTablaDatos> TableDisplay;

    @FXML
    private TableColumn<MensajeTablaDatos, String> colFrom;
    @FXML
    private TableColumn<MensajeTablaDatos, String> colSubject;

    @FXML
    private TextArea Display;

    @FXML
    private Button btnConnect;
    @FXML
    private Button btnFillTable;
    @FXML
    private Button btnNewMail;

    POP3Client pop3 = new POP3Client();

    List<MensajeTablaDatos> tablaDatos = new ArrayList<>(); // Inicializamos la lista de datos de la tabla


    public void ConnectAndGetData() throws IOException {
        pop3.connect("mail.damiansu.com");
        System.out.println("NOS HEMOS CONECTADO");

        if (!pop3.login("clientecachibache@damiansu.com", "Grupocdmfp2023")) {
            System.out.println("FALLO EN EL LOGIN");
        } else {
            System.out.println("ESTAMOS LOGEADOS");

        }


    }


    public void fillTable() throws IOException {
        // Almacenamos los mensajes en un array
        POP3MessageInfo[] mensajes = pop3.listMessages();

        for (POP3MessageInfo mensaje : mensajes) {
            Reader mensajeReader = pop3.retrieveMessage(mensaje.number);
// Almacenamos los datos del header (from, subject, date y length)
            String from = null;
            String subject = null;
            String date = null;
            int contentLength = 0;

            StringBuilder headerBuilder = new StringBuilder();

            int c; // No se que es esto

            while ((c = mensajeReader.read()) != -1) {
                headerBuilder.append((char) c);

                if (c == '\r') {
                    c = mensajeReader.read();

                    if (c == '\n') {
                        String headerLine = headerBuilder.toString();

                        if (headerLine.startsWith("From:")) {
                            from = headerLine.substring(5).trim();
                        } else if (headerLine.startsWith("Subject:")) {
                            subject = headerLine.substring(8).trim();
                        } else if (headerLine.startsWith("Date:")) {
                            date = headerLine.substring(5).trim();
                        } else if (headerLine.startsWith("Content-Length:")) {
                            contentLength = Integer.parseInt(headerLine.substring(15).trim());
                        }
                        headerBuilder.setLength(0);

                        if (headerLine.isEmpty()) {
                            mensajeReader.reset(); // Necesario para posicionar el reader al principio de nuevo, sino no muestra el contenido del correo
                            break;
                        }

                    }
                }

            }


            // Esto es necesario para extraer solo el mensaje dentro del mail
            Reader contentStream = pop3.retrieveMessage(mensaje.number);
            BufferedReader contentReader = new BufferedReader(contentStream);
            boolean inPlainText = false;
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = contentReader.readLine()) != null) {
                if (line.startsWith("Content-Type: text/plain")) {
                    inPlainText = true;
                } else if (line.startsWith("Content-Type:")) {
                    inPlainText = false;
                } else if (inPlainText) {
                    contentBuilder.append(line);
                }
            }

            // Esto es necesario para extraer solo el mensaje dentro del mail
            // Leemos y mostramos el contenido del mensaje
            char[] contenido = new char[contentLength];
            int charLeidos = mensajeReader.read(contenido, 0, contentLength);


            // Añadimos a la clase MensajeTablaDatos los datos del from y el subject
            MensajeTablaDatos mensajeTablaDatos = new MensajeTablaDatos(from, subject, contentBuilder.toString());
            tablaDatos.add(mensajeTablaDatos);


        }

        // Creamos la lista observable a partir de la lista de datos de la tabla
        ObservableList<MensajeTablaDatos> data = FXCollections.observableArrayList(tablaDatos);
        // asignar las propiedades cellValueFactory a las columnas
        colFrom.setCellValueFactory(new PropertyValueFactory<MensajeTablaDatos, String>("from"));
        colSubject.setCellValueFactory(new PropertyValueFactory<MensajeTablaDatos, String>("subject"));

        TableDisplay.setItems(data);


    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        btnConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    pop3.connect("mail.damiansu.com");
                    System.out.println("NOS HEMOS CONECTADO");
                    ConnectAndGetData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnFillTable.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                try {


                    fillTable();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // agregar el EventHandler para cuando se seleccione una fila de la tabla
        TableDisplay.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {

                String from = newSelection.getFrom();
                String subject = newSelection.getSubject();
                String mensaje = newSelection.getMensaje();

                String mensajeCompleto = "From: " + from + "\nSubject: " + subject + "\n\nMENSAJE: " + mensaje;
                Display.setText("");
                Display.appendText(mensajeCompleto);

            }
        });
    }


    public class MensajeTablaDatos {
        private String from;
        private String subject;
        private String mensaje;

        public MensajeTablaDatos(String from, String subject, String mensaje) {
            this.from = from;
            this.subject = subject;
            this.mensaje = mensaje;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
    }
}


