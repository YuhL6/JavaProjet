package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class InfoControl {
    @FXML
    private Label label;
    private Stage stage;
    public void setInfo(String info){
        label.setText(info);
    }
    public void setStage(Stage stage){
        this.stage = stage;
    }
    @FXML
    private void OKHandler(){
        stage.close();
    }
}
