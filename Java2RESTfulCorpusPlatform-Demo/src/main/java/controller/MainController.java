package controller;

import com.alibaba.fastjson.JSON;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import util.Response;

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
    private TableColumn checkColumn;
    @FXML
    private TextField searchField;
    @FXML
    private TextArea textArea;
    private ObservableList<Document> observableList = FXCollections.observableArrayList();
    private String lastSearchString;
    private int lastSelectedIndex = -1;

    @FXML
    private void initialize() throws IOException {
        try {
            // get information from server, the difficulty is how to transfer object between sockets, try fastjson.
            String res = Request.Get(endpoint + "/").execute().returnContent().asString();
            Response response = JSON.parseObject(res, Response.class);
            List<Document> list = JSON.parseArray(response.getResult().get("files").asText(), Document.class);
            observableList.addAll(list);
        }catch (IOException e){
            showInfo("Cannot connect to server");
            Platform.exit();
            return;
        }
        tableView.setItems(observableList);
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        showDocumentContent(null);
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        showDocumentContent(newValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        refresh();
    }

    private void refresh(){
        checkColumn.setCellFactory(col ->{
            TableCell<Document, String> tableCell = new TableCell<Document, String>(){
                @Override
                public void updateItem(String item, boolean empty){
                    super.updateItem(item, empty);
                    if (!empty) {
                        // each sort of table column will execute this part of code
                        // checkBox will update each time
                        // make sure the addListener instruction executes after the setSelected instruction
                        Document document = tableView.getItems().get(this.getIndex());
                        CheckBox checkBox = new CheckBox();
                        checkBox.setSelected(document.getSelected());
                        checkBox.selectedProperty().addListener(event -> {
                            try {
                                if (checkBox.isSelected() && !chosenList.contains(document)) {
                                    chosenList.add(document);
                                    document.setSelected(true);
                                }else if (!checkBox.isSelected()){
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

    private void showDocumentContent(Document document) throws IOException {
        if (document == null) {
            textArea.setText("");
            return;
        }
        textArea.setText(document.getContent());
    }

    public void existsHandler() throws IOException {
        // ask the server about the md5
        File file;
        int length;
        try{
            file = fileBrowserHandler();
            length = (int) file.length();
        }catch (Exception e){
            return;
        }
        byte[] bytes = new byte[length];
        Document document = new Document();
        // BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        // bufferedReader.read();
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(bytes);
        document.setContent(new String(bytes));
        for (Document d: observableList){
            if (d.getHash().equals(document.getHash())){
                tableView.getSelectionModel().select(d);
                return;
            }
        }
        // the file not found in local host, ask server to search it
        // if in the server, pass the document information to localhost
        String res = Request.Get(endpoint + "/exists/" + document.getHash()).execute().returnContent().asString();
        Response response = JSON.parseObject(res, Response.class);
        if (!response.getResult().get("exists").asBoolean()){
            showInfo("The file not exists");
        }else {
            document.setName(response.getResult().get("fileName").asText());
            observableList.add(document);
            tableView.getSelectionModel().select(document);
        }
    }

    private void showInfo(String info) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("Info.fxml"));
        Parent root = fxmlLoader.load();
        InfoControl controller = fxmlLoader.getController();
        controller.setInfo(info);
        controller.setStage(stage);
        stage.setScene(new Scene(root));
        stage.showAndWait();
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
        byte[] bytes = new byte[(int) file.length()];
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("AddFile.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        AddFileController controller = fxmlLoader.getController();
        controller.setFileName(fileName);
        controller.setStage(stage);
        stage.setScene(new Scene(root));
        stage.showAndWait();
        if (controller.getOK()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
            Document d = new Document();
            d.setContent(new String(bytes));
            d.setName(controller.getFileName());
            upload(d);
        }
    }

    @FXML
    public void searchHandler(){
        String searchString = searchField.getText().toLowerCase();
        Document selected = null;
        if (lastSearchString != null && lastSearchString.equalsIgnoreCase(searchString) && lastSelectedIndex != -1){
            for (int i = lastSelectedIndex + 1; i < observableList.size(); i++){
                if (observableList.get(i).getName().toLowerCase().contains(searchString)){
                    lastSelectedIndex = i;
                    selected = observableList.get(i);
                    break;
                }
            }
        }else{
            int i = 0;
            lastSearchString = searchString;
            for (Document document: observableList) {
                if (document.getName().toLowerCase().contains(searchString)) {
                    selected = document;
                    lastSelectedIndex = i;
                    break;
                }
                i++;
            }
        }
        if (selected != null)
            tableView.getSelectionModel().select(selected);
        else {
            lastSelectedIndex = -1;
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
            String name = d.getName().substring(0, d.getName().lastIndexOf("."));
            String format = d.getName().substring(d.getName().lastIndexOf("."));
            while (file.exists()){
                file = new File(path + File.separator + name + i + format);
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            Response response = JSON.parseObject(res, Response.class);
            writer.write(response.getResult().get("content").asText());
            writer.close();
            d.setSelected(false);
        }
        chosenList.clear();
        showInfo("Download Finish");
        refresh();
    }

    public void upload(Document d) throws IOException {
        // upload file
        String s = JSON.toJSONString(d);
        String res = Request.Post(endpoint+"/upload").bodyByteArray(s.getBytes(StandardCharsets.UTF_8)).execute().returnContent().asString();
        Response response = JSON.parseObject(res, Response.class);
        if (response.getResult().get("success").asBoolean()) {
            showInfo("Upload Successfully");
            observableList.add(d);
        }
        else{
            showInfo(response.getMessage());
        }
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
