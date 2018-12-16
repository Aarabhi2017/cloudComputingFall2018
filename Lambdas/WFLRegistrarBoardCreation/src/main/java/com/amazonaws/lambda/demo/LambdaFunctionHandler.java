package com.amazonaws.lambda.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.csye.fall2018.studentInformationSystems.datamodels.*;
import com.google.gson.*;

public class LambdaFunctionHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	AmazonDynamoDB amazonDynamoDB;
	DynamoDBMapper dynamoDBMapper;
	DynamoDBConnector dynamoDBConnector;

	public void connectToStudentSystem() {

		
		dynamoDBConnector = new DynamoDBConnector();
		DynamoDBConnector.init();
		dynamoDBMapper = new DynamoDBMapper(dynamoDBConnector.getClient());

		System.out.println("\nClient Created\n");
	}
	public Course getCourse(String courseId) {
		HashMap<String, String> expressionAttributesNames = new HashMap<>();
		expressionAttributesNames.put("#courseId", "courseId");

		HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
		expressionAttributeValues.put(":courseId", new AttributeValue().withS(courseId));

		DynamoDBQueryExpression<Course> dynamoDBQueryExpression = new DynamoDBQueryExpression<Course>()
				.withIndexName("courseId-index").withKeyConditionExpression("#courseId = :courseId")
				.withExpressionAttributeNames(expressionAttributesNames)
				.withExpressionAttributeValues(expressionAttributeValues).withConsistentRead(false);

		List<Course> Course = dynamoDBMapper.query(Course.class, dynamoDBQueryExpression);
		if (Course != null) {
			return Course.get(0);
		}
		return null;
	}
	public void createBoard(String courseId, String boardId)
	{
		Course course = getCourse(courseId);

		Board board = null;
		if (course != null) {
			board = new Board(course.getCourseId(), boardId);
		}
		if (board != null) {
			course.setBoard(boardId);
			dynamoDBMapper.save(board);
			dynamoDBMapper.save(course);
		}

	}
	public void sendPost(String jsonRegistrar)
	{
		try
		{
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost("http://Studentinformationsystems-env-1.ddbhv3xc9a.us-east-1.elasticbeanstalk.com/webapi/registrar/addRegistrar");

			httppost.setEntity(new StringEntity(jsonRegistrar));
			httppost.setHeader("Content-type", "application/json");

			httpclient.execute(httppost);
		}
		catch(Exception e)
		{
			
		}
		
	}
    @Override
    public Map<String, Object> handleRequest(Map<String, Object> json, Context context) {
        Map<String,String> m1=(Map<String,String>)json.get("courseId");
       // Map<String,String> m2=(Map<String,String>)json.get("Board");
        String courseId = m1.get("s");
        
        Map<String,String> m2=(Map<String,String>)json.get("Department");
        // Map<String,String> m2=(Map<String,String>)json.get("Board");
         String department = m2.get("s");
         
         
        connectToStudentSystem();
        createBoard(courseId,"B"+courseId);   
        
        Registrar r = new Registrar("OFFER"+courseId, department, "course", "5000");
        //dynamoDBMapper.save(r);
        Gson gson = new Gson();
        String jsonRegistrar=gson.toJson(r);
        sendPost(jsonRegistrar);
        
        // TODO: implement your handler
        return json;
    }

}
