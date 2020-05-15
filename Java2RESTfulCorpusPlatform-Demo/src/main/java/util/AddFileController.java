package util;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddFileController {
    private Stage stage;
    private String fileName;
    private String comment;
    private boolean isOK = false;
    @FXML
    private TextField nameText;
    @FXML
    private TextArea commentArea;
    @FXML
    public void OKButton(){
        fileName = nameText.getText();
        comment = commentArea.getText();
        isOK = true;
        stage.close();
    }
    public void setFileName(String str){
        fileName = str;
        nameText.setText(str);
    }
    public void setComment(String str){
        comment = str;
    }
    public void setStage(Stage stage){
        this.stage = stage;
    }

    public boolean getOK(){
        return isOK;
    }
}
