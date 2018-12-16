package com.csye.fall2018.studentInformationSystems.datamodels;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Registrar")
@DynamoDBDocument
public class Registrar {

	private String department;
	private String offeringId;
	private String offeringType;
	private String registrationId;
	private String perUnitPrice;

	public Registrar() {

	}

	public Registrar(String offeringId, String dept, String offeringType, String perUnitPrice) {
		this.offeringId = offeringId;
		this.department = dept;
		this.offeringType = offeringType;
		this.perUnitPrice = perUnitPrice;
	}

	public Registrar(String offeringId, String dept, String offeringType, String TAId, String perUnitPrice) {
		this.offeringId = offeringId;
		this.department = dept;
		this.offeringType = offeringType;
		this.perUnitPrice = perUnitPrice;
	}

	@DynamoDBAutoGeneratedKey
	@DynamoDBHashKey
	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String id) {
		this.registrationId = id;
	}

	@DynamoDBAttribute(attributeName = "OfferingType")
	public String getOfferingType() {
		return offeringType;
	}

	public void setOfferingType(String offeringType) {
		this.offeringType = offeringType;
	}

	@DynamoDBAttribute(attributeName = "PerUnitPrice")
	public String getPerUnitPrice() {
		return perUnitPrice;
	}

	public void setBoard(String perUnitPrice) {
		this.perUnitPrice = perUnitPrice;
	}

	@DynamoDBIndexHashKey(attributeName = "offeringId", globalSecondaryIndexName = "offeringId-index")
	public String getOfferingId() {
		return offeringId;
	}

	public void setOfferingId(String offeringId) {
		this.offeringId = offeringId;
	}

	@DynamoDBAttribute(attributeName = "Department")
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String dept) {
		this.department = dept;
	}
}