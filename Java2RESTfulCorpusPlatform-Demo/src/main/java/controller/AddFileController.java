package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddFileController {
    private Stage stage;
    private boolean isOK = false;
    @FXML
    private TextField nameText;
    @FXML
    public void OKButton(){
        isOK = true;
        stage.close();
    }
    public void setFileName(String str){
        nameText.setText(str);
    }

    public String getFileName(){
        return nameText.getText();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public boolean getOK(){
        return isOK;
    }
}
