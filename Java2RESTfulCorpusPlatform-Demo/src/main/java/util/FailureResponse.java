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

}

