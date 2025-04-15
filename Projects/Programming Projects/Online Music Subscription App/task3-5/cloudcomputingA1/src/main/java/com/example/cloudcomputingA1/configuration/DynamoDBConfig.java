package com.example.cloudcomputingA1.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.example.cloudcomputingA1.Interface.StringToIntegerConverter;

@Configuration
public class DynamoDBConfig {
	
	
    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(buildAmazonDynamoDB());
    }
    
    private AmazonDynamoDB buildAmazonDynamoDB() {
    	return   AmazonDynamoDBClientBuilder
    			.standard()
        		.withRegion(Regions.US_EAST_1)
        		.withCredentials(new ProfileCredentialsProvider("default"))
        		.build();	
    }
}
