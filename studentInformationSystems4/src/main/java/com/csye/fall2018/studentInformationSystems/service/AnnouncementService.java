package com.csye.fall2018.studentInformationSystems.service;

import java.util.HashMap;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.csye.fall2018.studentInformationSystems.datamodels.Announcements;
import com.csye.fall2018.studentInformationSystems.datamodels.DynamoDBConnector;

public class AnnouncementService {

	DynamoDBMapper dynamoDBMapper;
	DynamoDBConnector dynamoDBConnector;
	String tableName = "Announcements";

	BoardService boardService = new BoardService();

	public AnnouncementService() {
		dynamoDBConnector = new DynamoDBConnector();
		DynamoDBConnector.init();
		dynamoDBMapper = new DynamoDBMapper(dynamoDBConnector.getClient());
	}

	public List<Announcements> getAllAnnouncements() {
		List<Announcements> list = dynamoDBMapper.scan(Announcements.class, new DynamoDBScanExpression());
		return list;
	}

	public Announcements getAnnouncement(String boardId_announcementId) {
	
		String[] components = boardId_announcementId.split("_");
		HashMap<String, String> expressionAttributesNames = new HashMap<>();
		expressionAttributesNames.put("#BoardId", "BoardId");
		expressionAttributesNames.put("#AnnouncementId", "AnnouncementId");
		
		HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
		expressionAttributeValues.put(":BoardId", new AttributeValue().withS(components[0]));
		expressionAttributeValues.put(":AnnouncementId", new AttributeValue().withS(components[1]));

		DynamoDBQueryExpression<Announcements> dynamoDBQueryExpression = new DynamoDBQueryExpression<Announcements>()
				.withIndexName("BoardId-AnnouncementId-index").withKeyConditionExpression("#BoardId = :BoardId and #AnnouncementId = :AnnouncementId")
				.withExpressionAttributeNames(expressionAttributesNames)
				.withExpressionAttributeValues(expressionAttributeValues).withConsistentRead(false);

		List<Announcements> AnnouncementList = dynamoDBMapper.query(Announcements.class, dynamoDBQueryExpression);
		if (AnnouncementList.isEmpty()) {
			return null;
		}
		return AnnouncementList.get(0);
	}

	public boolean deleteAnnouncement(String boardId_announcementId) {
		Announcements announcement = getAnnouncement(boardId_announcementId);
		if (announcement != null) {
			dynamoDBMapper.delete(announcement);
			return true;
		}
		return false;
	}

	public List<Announcements> getAnnouncements(String boardId) {

		HashMap<String, String> expressionAttributesNames = new HashMap<>();
		expressionAttributesNames.put("#BoardId", "BoardId");

		HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
		expressionAttributeValues.put(":BoardId", new AttributeValue().withS(boardId));

		DynamoDBQueryExpression<Announcements> dynamoDBQueryExpression = new DynamoDBQueryExpression<Announcements>()
				.withIndexName("BoardId-AnnouncementId-index").withKeyConditionExpression("#BoardId = :BoardId")
				.withExpressionAttributeNames(expressionAttributesNames)
				.withExpressionAttributeValues(expressionAttributeValues).withConsistentRead(false);

		List<Announcements> AnnouncementList = dynamoDBMapper.query(Announcements.class, dynamoDBQueryExpression);
		if (AnnouncementList.isEmpty()) {
			return null;
		}
		return AnnouncementList;
	}

	public Announcements addAnnouncement(String boardId, String announcementId, String text) {

		if (text.length() > 160) {
			text = text.substring(0, 160);
		}
		Announcements ann = new Announcements(announcementId, boardId, text);
		dynamoDBMapper.save(ann);
		return ann;
	}

	public Announcements changeAnnouncement(String boardId_announcementId, String text) {
		Announcements ann = getAnnouncement(boardId_announcementId);
		if(text.length() > 160)
		{
			text = text.substring(0, 160);
		}
		ann.setAnnouncementText(text);
		dynamoDBMapper.save(ann);
		return ann;
	}

}
