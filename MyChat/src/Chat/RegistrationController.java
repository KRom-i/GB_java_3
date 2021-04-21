package Chat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RegistrationController {

    @FXML
    private VBox WindowReg;
    @FXML
    private TextField nick;
    @FXML
    private TextField log;
    @FXML
    private PasswordField pass;

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String result;

    public void regCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) WindowReg.getScene().getWindow();
        stage.close();
    }

    public void singUp(ActionEvent actionEvent) {
        if (log.getText().length() == 0){
            logColor(false);
            log.clear();
        } else if  (pass.getText().length() == 0) {
            passColor(false);
            pass.clear();
        } else if  (nick.getText().length() == 0) {
            nickColor(false);
            nick.clear();
        } else {
            try {
                socket = new Socket(Controller.ADDRESS, Controller.PORT);
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
                out.writeUTF("/signup " + log.getText() + " " + pass.getText() + " " + nick.getText());
                result = in.readUTF();
                if (result.equals("login failed")){
                    logColor(false);
                    log.clear();
                }  else if (result.equals("nickname failed")){
                    nickColor(false);
                    nick.clear();
                } else if (result.equals("Successful registration")){
                    successfulReg();
                } else {
                    logColor(false);
                    passColor(false);
                    nickColor(false);
                    log.clear();
                    nick.clear();
                    pass.clear();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void successfulReg(){
        log.setStyle("-fx-background-color: #7FFF00");
        pass.setStyle("-fx-background-color: #7FFF00");
        nick.setStyle("-fx-background-color: #7FFF00");
    }

    public void clearText(){
        logColor(true);
        passColor(true);
        nickColor(true);
    }

    public void logColor(Boolean bool){
        if (bool){
            log.setStyle("-fx-background-color: #696969");
        } else {
            log.setStyle("-fx-background-color: #800000");
        }
    }

    public void passColor(Boolean bool){
        if (bool){
            pass.setStyle("-fx-background-color: #696969");
        } else {
            pass.setStyle("-fx-background-color: #800000");
        }
    }

    public void nickColor(Boolean bool){
        if (bool){
            nick.setStyle("-fx-background-color: #696969");
        } else {
            nick.setStyle("-fx-background-color: #800000");
        }
    }


}
