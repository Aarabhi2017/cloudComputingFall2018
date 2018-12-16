package com.csye.fall2018.studentInformationSystems.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.csye.fall2018.studentInformationSystems.datamodels.DynamoDBConnector;
import com.csye.fall2018.studentInformationSystems.datamodels.Registrar;

public class RegistrarService {

	DynamoDBMapper dynamoDBMapper;
	DynamoDBConnector dynamoDBConnector;
	
	public RegistrarService() {
		dynamoDBConnector = new DynamoDBConnector();
		DynamoDBConnector.init();
		dynamoDBMapper = new DynamoDBMapper(dynamoDBConnector.getClient());
	}
	
	public Registrar addNewRegistrar(Registrar reg) {
		dynamoDBMapper.save(reg);
		return reg;
	}

}
