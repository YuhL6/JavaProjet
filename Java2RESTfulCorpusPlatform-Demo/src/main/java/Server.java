import com.alibaba.fastjson.JSON;
import dao.TextDao;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.Document;
import util.FailureCause;
import util.FailureResponse;
import util.SuccessResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

//import dao.TextDao;
//import io.javalin.Javalin;
//import io.swagger.v3.oas.models.info.Info;
//import io.javalin.plugin.openapi.OpenApiOptions;
//import io.javalin.plugin.openapi.OpenApiPlugin;
//import io.javalin.plugin.openapi.ui.ReDocOptions;
//import io.javalin.plugin.openapi.ui.SwaggerOptions;
//import org.sql2o.Sql2o;
//import service.TextService;
//
//public class Server {
//    public static void main(String[] args) throws ClassNotFoundException {
//        //TODO:connect database
//
//
//
//
//        TextDao dao;
//        TextService service = new TextService(dao);
//
//        Javalin app = Javalin.create(config -> {
//            config.registerPlugin(getConfiguredOpenApiPlugin());
//        }).start(7001);
//        app.get("/", ctx -> ctx.result("Welcome to RESTful Corpus Platform"));
//        // handle exist
//        app.get("/files/:md5/exists", service::handleExists);
//        // handle upload
//        app.post("/files/:md5", service::handleUpload);
//        // handle compare
//        app.get("/files/:md51/compare/:md52", service::handleCompare);
//        // handle download
//        app.get("/files/:md5", service::handleDownload);
//    }
//
//
//    private static OpenApiPlugin getConfiguredOpenApiPlugin() {
//        Info info = new Info().version("1.0").description("RESTful Corpus Platform API");
//        OpenApiOptions options = new OpenApiOptions(info)
//                .activateAnnotationScanningFor("cn.edu.sustech.java2.RESTfulCorpusPlatform")
//                .path("/swagger-docs") // endpoint for OpenAPI json
//                .swagger(new SwaggerOptions("/swagger-ui")); // endpoint for swagger-ui
////                .reDoc(new ReDocOptions("/redoc")); // endpoint for redoc
//        return new OpenApiPlugin(options);
//    }
//}
public class Server{
    public static void main(String[] args){
        Javalin app = Javalin.create().start(7002);
        TextDao dao = new TextDao();
        dao.getConnection();
        app.get("/", ctx -> {
            String str = dao.getAll();
            sendResult(ctx, "files", str, null);
        });
        app.get("/download/:hash", ctx -> {
            String result = dao.getDetail(ctx.pathParam("hash"));
            sendResult(ctx, "content", result, FailureCause.FILE_NOT_FOUND);
        });
        app.post("/upload", ctx -> {
            Document d = JSON.parseObject(ctx.body(), Document.class);
            Document d1 = new Document();
            d1.setContent(d.getContent());
            if (!d1.getHash().equalsIgnoreCase(d.getHash())){
                sendFailureResult(ctx, "success", false, FailureCause.HASH_NOT_MATCH);
            }else if (dao.hasHash(d.getHash()))
                sendFailureResult(ctx, "success", false, FailureCause.ALREADY_EXIST);
            else{
                dao.insert(d);
                sendResult(ctx, "success", "true", null);
            }
        });
        app.get("/exists/:hash", ctx -> {
            String s = dao.getDocument(ctx.pathParam("hash"));
            if (s.equals(""))
                sendSuccessResult(ctx, "exists", false);
            else{
                SuccessResponse response = new SuccessResponse();
                response.getResult().put("exists", true);
                response.getResult().put("fileName", s);
                ctx.result(response.toString());
            }
        });

    }

    private static void sendSuccessResult(Context ctx, String key, boolean value){
        SuccessResponse successResponse = new SuccessResponse();
        successResponse.getResult().put(key, value);
        ctx.result(successResponse.toString());
    }

    private static void sendFailureResult(Context ctx, String key, boolean value, FailureCause failureCause){
        FailureResponse failureResponse = new FailureResponse(failureCause);
        failureResponse.getResult().put(key, value);
        ctx.result(failureResponse.toString());
    }

    private static void sendResult(Context ctx, String key, String value, FailureCause failureCause) {
        if (value.equalsIgnoreCase("")){
            FailureResponse failureResponse = new FailureResponse(failureCause);
            ctx.result(failureResponse.toString());
        }else {
            SuccessResponse successResponse = new SuccessResponse();
            successResponse.getResult().put(key, value);
            ctx.result(successResponse.toString());
        }
    }
}