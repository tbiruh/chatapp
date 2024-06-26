
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client extends Application {

    private static Socket socketObj;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private TextArea showMsg;
    private TextField writeMsg;
    private Button sendMsg;

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        showMsg = new TextArea();
        showMsg.setEditable(false);
        showMsg.setPrefSize(300, 200);

        writeMsg = new TextField();
        writeMsg.setPromptText("Type Here!!");

        sendMsg = new Button("SEND");
        sendMsg.setOnAction(event -> sendMsgAction());

        root.getChildren().addAll(showMsg, writeMsg, sendMsg);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Client");
        primaryStage.show();

        connectToServer();
    }

    private void connectToServer() {
        try {
            socketObj = new Socket("localhost", 7777);
            dis = new DataInputStream(socketObj.getInputStream());
            dos = new DataOutputStream(socketObj.getOutputStream());

            new Thread(() -> {
                try {
                    String msgFromServer;
                    while ((msgFromServer = dis.readUTF()) != null) {
                        final String finalMsgFromServer = msgFromServer; // make msgFromServer effectively final
                        Platform.runLater(() -> showMsg.appendText("Server: " + finalMsgFromServer + "\n"));
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> showMsg.appendText("Server Disconnected\n"));
                }
            }).start();

        } catch (Exception e) {
            showMsg.appendText("Server Not Connected\n");
        }
    }

    private void sendMsgAction() {
        try {
            String msg = writeMsg.getText();
            dos.writeUTF(msg);
            dos.flush(); // Ensure data is sent immediately
            writeMsg.clear();
        } catch (Exception e) {
            showMsg.appendText("Message couldn't send!!\n");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
