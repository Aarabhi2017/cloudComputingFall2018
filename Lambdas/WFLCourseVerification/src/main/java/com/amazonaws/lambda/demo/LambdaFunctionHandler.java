package com.amazonaws.lambda.demo;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;

public class LambdaFunctionHandler implements RequestHandler<Map<String, Object> , Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> json , Context context) {
        context.getLogger().log("Received json: " + json);
        Map<String,String> m1=(Map<String,String>)json.get("Board");
        Map<String,List<String>> m2=(Map<String,List<String>>)json.get("Roster");
        Map<String,String> m3=(Map<String,String>)json.get("notificationTopic");
        
        boolean isNewCourse = m1.get("s").equals(" ") && m2.get("l").isEmpty() && 
        		m3.get("s").equals(" ");
        context.getLogger().log("Isnull:"+m1.get("s").equals(" "));
        context.getLogger().log("Isnull:"+m2.get("l").isEmpty());
        context.getLogger().log("Isnull:"+m3.get("s").equals(" "));
        json.put("isNewCourse", isNewCourse);
        return json;
     
        
    }
}