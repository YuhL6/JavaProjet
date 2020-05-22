import com.alibaba.fastjson.JSON;
import dao.TextDao;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.Document;
import org.apache.commons.lang3.StringUtils;
import util.FailureCause;
import util.FailureResponse;
import util.SuccessResponse;
import java.util.List;

/**
 * A server based on JavaLin connects with front-ends and database. Port number is 7002. <br>
 *     The <code>Server</code> supports following urls:<br>
 *         &ensp;1. <code>/download/hash</code>: hash is the md5 hash value of the file user wants to download. Server would
 *         return the String content in UTF-8 encoding.<br>
 *         &ensp;2. <code>/exists/hash</code>: hash is the md5 hash value of the file user wants to check whether exists or not. Server
 *         would return a boolean value.<br>
 *         &ensp;3. <code>/compare/hash1/hash2</code>:hash1 and hash2 is the md5 hash value of the files user wants to compare.
 *         Server would return two values: simple similarity and Levenshtein distance.<br>
 *         &ensp;4. <code>/upload/</code>: user is expected to upload a JSON format file which can be deserialize to Document.class by FastJSON.<br>
 *         &ensp;5. <code>/</code>: server would return a list of documents in JSON format. <B>Note</B>: when the content's
 *         length is larger than 100, server would return a preview of the content, i.e. the substring of 100 chars.
 */

public class Server{
    public static void main(String[] args){
        int portNumber = 7002;
        Javalin app = Javalin.create().start(portNumber);
        TextDao dao = new TextDao();
        dao.getConnection();
        app.get("/", ctx -> {
            List<Document> list = dao.getAll();
            SuccessResponse response = new SuccessResponse();
            response.getResult().setList(list);
            String res = response.toString();
            ctx.result(res);
        });
        app.get("/download/:hash", ctx -> {
            String result = dao.getDetail(ctx.pathParam("hash"));
            if (result.equalsIgnoreCase("")){
                FailureResponse failureResponse = new FailureResponse(FailureCause.FILE_NOT_FOUND);
                ctx.result(failureResponse.toString());
            }else {
                SuccessResponse successResponse = new SuccessResponse();
                successResponse.getResult().setContent(result);
                ctx.result(successResponse.toString());
            }
        });
        app.post("/upload", ctx -> {
            Document d = JSON.parseObject(ctx.body(), Document.class);
            Document d1 = new Document();
            d1.setContent(d.getContent());
            try {
                if (!d1.getHash().equalsIgnoreCase(d.getHash())) {
                    sendFailureResult(ctx, FailureCause.HASH_NOT_MATCH);
                }
            }catch(Exception e){
                // the content is null, which is not allowed, should be detected by front-end, which means user
                // not use front-end to get to this
                return;
            }
            if (dao.hasHash(d.getHash()))
                sendFailureResult(ctx, FailureCause.ALREADY_EXIST);
            else{
                dao.insert(d);
                SuccessResponse successResponse = new SuccessResponse();
                successResponse.getResult().setSuccess(true);
                ctx.result(successResponse.toString());
            }
        });
        app.get("/exists/:hash", ctx -> {
            String s = dao.getDocument(ctx.pathParam("hash"));
            if (s.equals("")) {
                SuccessResponse response = new SuccessResponse();
                response.getResult().setExists(false);
                ctx.result(response.toString());
            }
            else{
                SuccessResponse response = new SuccessResponse();
                response.getResult().setExists(true);
                response.getResult().setFileName(s);
                ctx.result(response.toString());
            }
        });
        app.get("/compare/:hash1/:hash2", context -> {
            String s1 = context.pathParam("hash1");
            String s2 = context.pathParam("hash2");
            String text1 = dao.getDetail(s1);
            String text2 = dao.getDetail(s2);
            int ldst = StringUtils.getLevenshteinDistance(text1, text2);
            double sim = simple_similarity(text1, text2);
            SuccessResponse successResponse = new SuccessResponse();
            successResponse.getResult().setSimple_similarity(sim);
            successResponse.getResult().setLevenshtein_distance(ldst);
            context.result(successResponse.toString());
        });
    }

    private static void sendFailureResult(Context ctx, FailureCause failureCause){
        FailureResponse failureResponse = new FailureResponse(failureCause);
        failureResponse.getResult().setSuccess(false);
        ctx.result(failureResponse.toString());
    }

    private static double simple_similarity(String text1, String text2){
        int length = Math.min(text1.length(), text2.length());
        int similar = 0;
        for (int i = 0; i< length; i++){
            if (text1.toLowerCase().charAt(i) == text2.toLowerCase().charAt(i))
                similar++;
        }
        return similar*1.0/length;
    }
}