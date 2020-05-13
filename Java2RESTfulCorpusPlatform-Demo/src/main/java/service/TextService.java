package service;

import dao.TextDao;
import io.javalin.http.Context;

import java.io.IOException;

public class TextService {

    TextDao dao;

    public TextService(TextDao dao) {
        this.dao = dao;
    }

    public void handleExists(Context ctx){

    }

    public void handleUpload(Context ctx) throws IOException {

    }

    public void handleDownload(Context ctx){

    }

    public void handleCompare(Context ctx){

    }

}
