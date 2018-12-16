package com.amazonaws.lambda.demo;

import java.util.Map;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.google.gson.Gson;

public class InvokeStepFunctionLambda implements RequestHandler<DynamodbEvent, String> {

	AWSStepFunctions stepfunction;

	public void connectToStepFunction() {
		stepfunction = AWSStepFunctionsClientBuilder.standard()
				.withCredentials(new DefaultAWSCredentialsProviderChain()).withRegion("us-east-1").build();

		System.out.println("\nClient Created\n");
	}

	@Override
	public String handleRequest(DynamodbEvent event, Context context) {
		context.getLogger().log("Received event: " + event);

		connectToStepFunction();
		Gson gson = new Gson();

		for (DynamodbStreamRecord record : event.getRecords()) {

			if (!record.getEventName().equals("REMOVE")) {
				Map<String, AttributeValue> map = record.getDynamodb().getNewImage();
				// 1. Invoke step function

				
				String jsonString = gson.toJson(map);

				context.getLogger().log("JSON String" + jsonString);

				stepfunction.startExecution(new StartExecutionRequest()
						.withStateMachineArn("arn:aws:states:us-east-1:617558986222:stateMachine:my_workflow")
						.withInput(jsonString));

			}
		}
		return "Processed records";
	}

}
