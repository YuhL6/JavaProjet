package util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class Response {
    public static class ResultSerializer implements ObjectSerializer{
        @Override
        public void write(JSONSerializer jsonSerializer, Object o, Object o1, Type type, int i) throws IOException {
            ObjectNode objectNode = (ObjectNode)o;
            jsonSerializer.write(objectNode.toString());
        }
    }
    public static class ResultDerializer implements ObjectDeserializer{

        @Override
        public <T> T deserialze(DefaultJSONParser defaultJSONParser, Type type, Object o) {
            JSONObject jsonObject = JSON.parseObject(defaultJSONParser.getLexer().stringVal());
            Set<Map.Entry<String, Object>> set = jsonObject.entrySet();
            ObjectNode objectNode = objectMapper.createObjectNode();
            for (Map.Entry e: set){
                objectNode.put(e.getKey().toString(), e.getValue().toString());
            }
            return (T) objectNode;
        }

        @Override
        public int getFastMatchToken() {
            return 0;
        }
    }
    int code;
    String message;
    @JSONField(serializeUsing = ResultSerializer.class, deserializeUsing = ResultDerializer.class)
    ObjectNode result;

    static ObjectMapper objectMapper = new ObjectMapper();

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
        this.result = objectMapper.createObjectNode();
    }

    public Response(int code, String message, ObjectNode result) {
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

    public ObjectNode getResult() {
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

    public Response setResult(ObjectNode result) {
        this.result = result;
        return this;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        Response.objectMapper = objectMapper;
    }

    public String toString(){
        return JSON.toJSONString(this);
    }
}
