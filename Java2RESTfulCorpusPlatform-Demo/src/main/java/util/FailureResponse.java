package util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FailureResponse extends Response {


    public FailureResponse(int code, String message) {
        super(code, message);
    }

    public FailureResponse(FailureCause failureCause){
        super(failureCause.code, failureCause.message);
    }

    public static void main(String[] args){
        SuccessResponse successResponse = new SuccessResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        successResponse.getResult().put("value", "false");
        successResponse.getResult().put("name", "yuh");
        Response response = JSON.parseObject(successResponse.toString(), Response.class);
        System.out.println(successResponse);
        String str = response.getResult().asText();
        System.out.println(response.getResult().toString());
    }

}

