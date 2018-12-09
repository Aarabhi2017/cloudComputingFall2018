package com.csye.fall2018.studentInformationSystems.datamodels;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;

public class SNSConnector {

	static AmazonSNS sns;

	public static void init() {
		if (sns == null) {

			sns = AmazonSNSClientBuilder.standard().withCredentials(new DefaultAWSCredentialsProviderChain())
					.withRegion("us-east-1").build();

			System.out.println("\nClient Created\n");
		}
	}

	public AmazonSNS getClient() {
		return sns;
	}

}
