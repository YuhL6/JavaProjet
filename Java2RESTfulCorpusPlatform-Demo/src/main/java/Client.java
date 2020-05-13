import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.MainController;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
//import org.apache.http.client.fluent.Request;
//import util.Response;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.Scanner;
//import java.util.stream.Collectors;
//
//public class Client{
//
//    static String endpoint = "http://localhost:7001/files";
//
//
//    enum Operation{
//        UPLOAD, DOWNLOAD, COMPARE, EXISTS
//    }
//    public static Operation parseOperation(String op){
//        //TODO: convert String to Operation
//    }
//    public static void main(String[] args) throws IOException {
//
//        while(true) {
//            Scanner in = new Scanner(System.in);
//            args = in.nextLine().split("\\s+");
//
//            if (args.length < 2) {
//                System.out.println("Simple Client");
//                printUsage();
//                return;
//            }
//
//            Operation operation = parseOperation(args[0]);
//            if (operation == null) {
//                System.err.println("Unknown operation");
//                printUsage();
//                return;
//            }
//
//            switch (operation) {
//                case UPLOAD:
//                    handleUpload(args);
//                    break;
//                case DOWNLOAD:
//                    handleDownload(args);
//                    break;
//                case COMPARE:
//                    handleCompare(args);
//                    break;
//                case EXISTS:
//                    handleExists(args);
//                    break;
//            }
//        }
//    }
//
//
//    private static void handleExists(String[] args) throws IOException {
//
//    }
//
//    private static void handleCompare(String[] args) throws IOException {
//
//    }
//
//    private static void handleDownload(String[] args) throws IOException {
//
//    }
//
//    private static void handleUpload(String[] args) throws IOException {
//
//    }
//
//    private static void printUsage() {
//        System.out.println("Usage: [op] [params]");
//        System.out.println("Available Operation: upload, download, compare, exists");
//    }
//
//    // source: https://www.baeldung.com/java-md5
//    public static String calculateMD5(byte[] bytes){
//
//    }
//}
public class Client extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("MainUI.fxml"));
        Parent root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

}
