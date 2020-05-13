package util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Document;
import org.apache.http.client.fluent.Request;
import org.checkerframework.checker.units.qual.C;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class MainController {
    private String endpoint = "http://localhost:7002";
    private ObservableList<Document> documents = FXCollections.observableArrayList();
    private List<Document> chosenList = new ArrayList<>();
    @FXML
    private TableView<Document> tableView;
    @FXML
    private TableColumn<Document, String> nameColumn;
    @FXML
    private TableColumn<Document, String> commentColumn;
    @FXML
    private TableColumn checkColumn;
    @FXML
    private TextField textField;
    @FXML
    private TextArea textArea;
    private ObservableList<Document> observableList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        try {
            // get information from server, the difficulty is how to transfer object between sockets, try fastjson.
            String res = Request.Get(endpoint + "/").execute().returnContent().asString();
            List<Document> list = JSON.parseArray(res, Document.class);
            for (Document d: list)
                observableList.add(d);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Cannot connect to server");
            return;
        }
        tableView.setItems(observableList);
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        commentColumn.setCellValueFactory(cellData -> cellData.getValue().commentProperty());
        showDocumentContent(null);
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDocumentContent(newValue));
        checkColumn.setCellFactory(col ->{
            TableCell<Document, String> tableCell = new TableCell<Document, String>(){
                @Override
                public void updateItem(String item, boolean empty){
                    super.updateItem(item, empty);
                    if (!empty) {
                        CheckBox checkBox = new CheckBox();
                        IndexedCell cell = this;
                        checkBox.selectedProperty().addListener(event -> {
                            if (checkBox.isSelected()) {
                                chosenList.add(observableList.get(cell.getIndex()));
                            }else {
                                chosenList.remove(observableList.get(cell.getIndex()));
                            }
                        });
                        this.setGraphic(checkBox);
                    }else{
                        this.setGraphic(null);
                    }
                }
            };
            return tableCell;});
    }

    private void showDocumentContent(Document document){
        if (document == null) {
            textArea.setText("");
            return;
        }
        Request.Get(endpoint + "/show" + document.getHash());
    }

    public boolean existsCheck(String s) throws IOException {
        // ask the server about the md5
        String res = Request.Get(endpoint + "/exists/" + s).execute().returnContent().asString();
        if (res.equals("false"))
            return false;
        else
            return true;
    }

    public double compareCheck(String s1, String s2) throws IOException {
        // how to implement in GUI
        String res = Request.Get(endpoint + "/compare/" + s1 + "/" + s2).execute().returnContent().asString();
        return 0.0;
    }

    public boolean upload(byte[] bytes) throws IOException {
        // upload file
        String res = Request.Post(endpoint+"/upload/").bodyByteArray(bytes).execute().returnContent().asString();
        return true;
    }

    public boolean download() throws IOException {
        // clear the selected
        // download file/path
        // store the bytes
        String res;
        for (Document d: chosenList) {
            res = Request.Get(endpoint + "/download/" + d.getHash()).execute().returnContent().asString();
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] bytes = decoder.decode(res);

        }
        return true;
    }

}
