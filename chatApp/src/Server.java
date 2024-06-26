
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Application {

    private static ServerSocket ss;
    private static Socket socketObj;
    private static DataInputStream dis;
    private static DataOutputStream dos;

    private static TextArea showMsg;
    private static TextField writeMsg;
    private static Button sendMsg;
    private static Label serverLabel;

    @Override
    public void start(Stage primaryStage) {
        serverLabel = new Label("SERVER");
        serverLabel.setStyle("-fx-font-size: 24");

        showMsg = new TextArea();
        showMsg.setEditable(false);
        showMsg.setStyle("-fx-background-color: #9EAFB6");
        showMsg.setPrefHeight(277);

        writeMsg = new TextField();
        writeMsg.setText("Type Here !!");

        sendMsg = new Button("SEND");
        sendMsg.setStyle("-fx-font-size: 14");
        sendMsg.setOnAction(event -> sendMessage());

        VBox root = new VBox(10);
        root.getChildren().addAll(serverLabel, showMsg, writeMsg, sendMsg);

        Scene scene = new Scene(root, 760, 400);

        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() -> {
            try {
                ss = new ServerSocket(7777);
                socketObj = ss.accept();
                dis = new DataInputStream(socketObj.getInputStream());
                dos = new DataOutputStream(socketObj.getOutputStream());

                new Thread(() -> {
                    try {
                        String msgFromClient;
                        while ((msgFromClient = dis.readUTF()) != null) {
                            final String finalMsgFromClient = msgFromClient; // make msgFromClient effectively final
                            Platform.runLater(() -> showMsg.appendText("Client: " + finalMsgFromClient + "\n"));
                        }
                    } catch (IOException e) {
                        Platform.runLater(() -> showMsg.appendText("Client Disconnected\n"));
                    }
                }).start();

            } catch (IOException e) {
                Platform.runLater(() -> showMsg.setText("Client is not Connected!!"));
            }
        }).start();
    }

    private void sendMessage() {
        try {
            String msg = writeMsg.getText();
            dos.writeUTF(msg);
            dos.flush(); // Ensure data is sent immediately
            writeMsg.clear();
        } catch (IOException e) {
            showMsg.setText("Message couldn't send!!");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
