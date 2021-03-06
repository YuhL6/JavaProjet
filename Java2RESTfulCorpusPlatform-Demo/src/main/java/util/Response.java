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
    /**
     * The Fields class takes place with the previous ObjectNode. The reason is that some format differences would occur using FastJSON.<br>
     *     <code>Response</code> should contains <code>files</code>, <code>exists</code>, <code>success</code>,
     *     <code>simple similarity</code>, <code>Levenshtein Distance</code>, <code>content</code>, <code>filename</code>.<br>
     */
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

        public Integer getLevenshtein_distance() {
            return levenshtein_distance;
        }

        public void setLevenshtein_distance(Integer levenshtein_distance) {
            this.levenshtein_distance = levenshtein_distance;
        }

        public Double getSimple_similarity() {
            return simple_similarity;
        }

        public void setSimple_similarity(Double simple_similarity) {
            this.simple_similarity = simple_similarity;
        }

        private Integer levenshtein_distance = null;
        private Double simple_similarity = null;

        public String getContent() {
            return content;
        }

        public void setContent(String content){
            this.content = content;
        }

        private String content = null;

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

    /**
     * The filter is used to filter the null value of fields of class <class>Fields</class> when serializing.
     * @return the JSON string of Response class generated by FastJSON.
     */
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
