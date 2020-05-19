package util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import model.Document;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Response {
    public static class Fields{
        @JSONField(name = "files")
        private List<Document> list = null;

        public List<Document> getList() {
            return list;
        }

        public void setList(List<Document> list) {
            this.list = list;
        }

        public Boolean getExists() {
            return exists;
        }

        public void setExists(Boolean exists) {
            this.exists = exists;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        private Boolean exists = null;
        private Boolean success = null;
        private String fileName = null;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPreview() {
            return preview;
        }

        public void setPreview(String preview) {
            this.preview = preview;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        private String content = null;
        private String preview = null;
        private Integer length = null;
    }

    int code;
    String message;
    Fields result;

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
        this.result = new Fields();
    }

    public Response(int code, String message, Fields result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Fields getResult() {
        return result;
    }

    public Response setCode(int code) {
        this.code = code;
        return this;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public Response setResult(Fields result) {
        this.result = result;
        return this;
    }

    public String toString(){
        PropertyFilter filter = (source, key, value) -> {
            if (value == null) {
                return false;
            }
            
            return true;
        };
        return JSON.toJSONString(this, filter);
    }
}
