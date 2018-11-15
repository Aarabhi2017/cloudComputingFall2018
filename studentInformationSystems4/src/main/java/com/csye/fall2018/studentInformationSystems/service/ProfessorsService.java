package com.csye.fall2018.studentInformationSystems.service;

import java.util.HashMap;
import java.util.List;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.csye.fall2018.studentInformationSystems.datamodels.Professor;
import com.csye.fall2018.studentInformationSystems.datamodels.DynamoDBConnector;

public class ProfessorsService {
	DynamoDBMapper dynamoDBMapper;
	DynamoDBConnector dynamoDBConnector;
	String tableName = "Professor";

	public ProfessorsService() {
		dynamoDBConnector = new DynamoDBConnector();
		DynamoDBConnector.init();
		dynamoDBMapper = new DynamoDBMapper(dynamoDBConnector.getClient());
	}

	// Getting a list of all professor
	// GET "..webapi/professors"
	public List<Professor> getAllProfessors() {
		// Getting the list
		List<Professor> list = dynamoDBMapper.scan(Professor.class, new DynamoDBScanExpression());
		return list;
	}

	// Adding a professor
	public Professor addProfessor(Professor prof) {
		// Get count of table
		dynamoDBMapper.save(prof);
		return getProfessor(prof.getProfessorId());
	}

	// Getting One Professor
	public Professor getProfessor(String profId) {

		HashMap<String, String> expressionAttributesNames = new HashMap<>();
		expressionAttributesNames.put("#professorId", "professorId");

		HashMap<String, AttributeValue> expressionAttributeValues = new HashMap<>();
		expressionAttributeValues.put(":professorId", new AttributeValue().withS(profId));

		DynamoDBQueryExpression<Professor> dynamoDBQueryExpression = new DynamoDBQueryExpression<Professor>()
				.withIndexName("professorId-index")
				.withKeyConditionExpression("#professorId = :professorId")
				.withExpressionAttributeNames(expressionAttributesNames)
				.withExpressionAttributeValues(expressionAttributeValues).withConsistentRead(false);

		List<Professor> Professor = dynamoDBMapper.query(Professor.class, dynamoDBQueryExpression);
		if(Professor.isEmpty())
		{
			return null;
		}
		return Professor.get(0);

	}

	// Deleting a professor
	public boolean deleteProfessor(String profId) {
		Professor prof = getProfessor(profId);
		if(prof!=null)
		{
			dynamoDBMapper.delete(prof);
			return true;
		}
		return false;
	}

	// Updating Professor Info
	public Professor updateProfessorInformation(String profId, String department) {
		Professor professor = getProfessor(profId);
		professor.setDepartment(department);
		dynamoDBMapper.save(professor);
		return professor;
	}

	// Get professors in a department
	public List<Professor> getProfessorsByDepartment(String department) {
		// Getting the list
		HashMap<String, AttributeValue> expressionAttributeValue = new HashMap<String, AttributeValue>();
		expressionAttributeValue.put(":deptValue", new AttributeValue().withS(department));

		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
				.withFilterExpression("department = :deptValue")
				.withExpressionAttributeValues(expressionAttributeValue);

		List<Professor> professors = dynamoDBMapper.scan(Professor.class, scanExpression);
		return professors;
	}

}
