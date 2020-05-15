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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Document;
import org.apache.http.client.fluent.Request;
import org.checkerframework.checker.units.qual.C;

import javax.print.Doc;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainController {
    private String endpoint = "http://localhost:7002";
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
    @FXML
    private Button downloadButton;
    private ObservableList<Document> observableList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        try {
            // get information from server, the difficulty is how to transfer object between sockets, try fastjson.
            String res = Request.Get(endpoint + "/").execute().returnContent().asString();
            List<Document> list = JSON.parseArray(res, Document.class);
            observableList.addAll(list);
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
        refresh();
    }

    private void refresh(){
        checkColumn.setCellFactory(col ->{
            TableCell<Document, String> tableCell = new TableCell<Document, String>(){
                @Override
                public void updateItem(String item, boolean empty){
                    super.updateItem(item, empty);
                    if (!empty) {
                        Document document = tableView.getItems().get(this.getIndex());
                        CheckBox checkBox = new CheckBox();
                        checkBox.setSelected(document.getSelected());
                        checkBox.selectedProperty().addListener(event -> {
                            try {
                                if (!chosenList.contains(document)) {
                                    chosenList.add(document);
                                    document.setSelected(true);
                                }else {
                                    chosenList.remove(document);
                                    document.setSelected(false);
                                }
                            }catch (Exception e){System.out.println("error");}
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

    @FXML
    public void uploadFile() throws IOException {
        File file;
        String fileName;
        try {
            file = fileBrowserHandler();
            fileName = file.getName();
        }catch (Exception e){
            return;
        }
        String comment = new String();
        byte[] bytes = new byte[(int) file.length()];
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("AddFile.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        AddFileController controller = fxmlLoader.getController();
        controller.setFileName(fileName);
        controller.setComment(comment);
        controller.setStage(stage);
        stage.setScene(new Scene(root));
        stage.showAndWait();
        if (controller.getOK()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
            Document d = new Document();
            d.setBytes(bytes);
            d.setName(fileName);
            d.setComment(comment);
            if (upload(d))
                observableList.add(d);
        }
    }

    @FXML
    public void downloadFile() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select the path");
        Stage stage = new Stage();
        String path = directoryChooser.showDialog(stage).getAbsolutePath();
        String res;
        for (Document d: chosenList){
            res = Request.Get(endpoint + "/download/" + d.getHash()).execute().returnContent().asString();
            File file = new File(path + File.separator + d.getName());
            int i = 1;
            while (file.exists()){
                file = new File(path + File.separator + d.getName() + i);
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(res);
            writer.close();
            d.setSelected(false);
        }
        chosenList.clear();
        refresh();
    }

    public boolean upload(Document d) throws IOException {
        // upload file
        String s = JSON.toJSONString(d);
        String res = Request.Post(endpoint+"/upload").bodyByteArray(s.getBytes(StandardCharsets.UTF_8)).execute().returnContent().asString();
        return true;
    }

    @FXML
    public File fileBrowserHandler() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");
        Stage stage = new Stage();
        try {
            return fileChooser.showOpenDialog(stage);
        }catch (Exception e){
            return null;
        }
    }

}
