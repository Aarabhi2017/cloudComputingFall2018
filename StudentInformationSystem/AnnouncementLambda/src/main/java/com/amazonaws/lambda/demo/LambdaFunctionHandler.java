package com.amazonaws.lambda.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.Topic;

public class LambdaFunctionHandler implements RequestHandler<DynamodbEvent, String> {
	AmazonDynamoDB amazonDynamoDB;
	AmazonSNS sns;

	public AmazonSNS connectToSns() {

		sns = AmazonSNSClientBuilder.standard().withCredentials(new DefaultAWSCredentialsProviderChain())
				.withRegion("us-east-1").build();

		return sns;
	}

	public void connectToStudentSystem() {

		amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
				.withCredentials(new DefaultAWSCredentialsProviderChain()).withRegion("us-east-1").build();

		System.out.println("\nClient Created\n");
	}

	@Override
	public String handleRequest(DynamodbEvent event, Context context) {
		try {
			// Starting connections...
			connectToSns();
			connectToStudentSystem();

			for (DynamodbStreamRecord record : event.getRecords()) {
				context.getLogger().log("EventId:" + record.getEventID());
				context.getLogger().log("Event name:" + record.getEventName());
				context.getLogger().log("Event attributes:" + record.getDynamodb().getNewImage().toString());

				if (record.getEventName().equals("INSERT") || true) {

					// 1. Find boardId
					Map<String, AttributeValue> map = record.getDynamodb().getNewImage();
					String boardId = map.get("BoardId").getS();

					context.getLogger().log("BoardId:" + boardId);

					// 2. Find CourseId for this boardId
					String courseId = "";
					Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
					expressionAttributeValues.put(":val", new AttributeValue().withS(boardId));

					ScanRequest scanRequest = new ScanRequest().withTableName("Board")
							.withFilterExpression("BoardId = :val")
							.withExpressionAttributeValues(expressionAttributeValues);
					ScanResult result = amazonDynamoDB.scan(scanRequest);
					for (Map<String, AttributeValue> item : result.getItems()) {
						if (item.get("CourseId") != null) {
							courseId = item.get("CourseId").getS();
							break;
						}
						context.getLogger().log(item.toString());
					}

					// 3. Get Topic from Course Id

					String notificationTopic = "";
					Map<String, AttributeValue> expressionAttributeValues1 = new HashMap<String, AttributeValue>();
					expressionAttributeValues1.put(":val", new AttributeValue().withS(courseId));

					ScanRequest scanRequest1 = new ScanRequest().withTableName("Course")
							.withFilterExpression("courseId = :val")
							// .withProjectionExpression("Id")
							.withExpressionAttributeValues(expressionAttributeValues1);
					ScanResult result1 = amazonDynamoDB.scan(scanRequest1);
					for (Map<String, AttributeValue> item : result1.getItems()) {
						if (item.get("notificationTopic") != null) {
							notificationTopic = item.get("notificationTopic").getS();
							break;
						}
						context.getLogger().log(item.toString());
					}
					context.getLogger().log("Notification - Topic:" + notificationTopic);

					// 4. publish notifications to this topic
					List<Topic> topics = sns.listTopics().getTopics();
					String topicArn = "";
					for (Topic topic : topics) {
						if (topic.toString().contains(notificationTopic)) {
							topicArn = topic.getTopicArn();
							System.out.println(topic.toString());
							break;
						}
					}

					if (!topicArn.isEmpty()) {
						PublishRequest publishRequest = new PublishRequest(topicArn,
								map.get("AnnouncementText").getS());
						PublishResult publishResult = sns.publish(publishRequest);
						context.getLogger().log("Published Result " + publishResult.toString());
					} else {
						context.getLogger().log("No such Topic in SNS");
					}
				}
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}