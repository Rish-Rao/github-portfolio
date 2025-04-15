package com.example.cloudcomputingA1.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.AbstractDynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.example.cloudcomputingA1.entity.Subscription;
import com.example.cloudcomputingA1.entity.User;



@Repository
public class SubscriptionRepository {
	@Autowired
    private DynamoDBMapper dynamoDBMapper;
    
    
	public List<Subscription> getSubscriptions(String email) {
	   
		 Map<String, AttributeValue> map = new HashMap<>();
		    map.put(":val", new AttributeValue().withS(email));

		    DynamoDBQueryExpression<Subscription> queryExpression = new DynamoDBQueryExpression<Subscription>()
		            .withKeyConditionExpression("email = :val")
		            .withExpressionAttributeValues(map);

		    List<Subscription> subscriptions = dynamoDBMapper.query(Subscription.class, queryExpression);
		    for (Subscription subscription : subscriptions) {
		        String year = subscription.getYear().toString();
		        subscription.setYear(Integer.valueOf(year));
		    }
		    return subscriptions;
	}
	
	public void delete(String email, String title) {
	    Subscription subscriptionToDelete = new Subscription();
	    subscriptionToDelete.setEmail(email);
	    subscriptionToDelete.setTitle(title);
	    DynamoDBDeleteExpression deleteExpression = new DynamoDBDeleteExpression();
	    
		dynamoDBMapper.delete(subscriptionToDelete, deleteExpression);
	}
	
	
	
	public void save(Subscription subscription) {
	    // Convert year to String
	    subscription.setYear(subscription.getYear());

	    // Set the query expression
	    Map<String, AttributeValue> attributeValues = new HashMap<>();
	    attributeValues.put(":email", new AttributeValue().withS(subscription.getEmail()));
	    attributeValues.put(":title", new AttributeValue().withS(subscription.getTitle()));
	    DynamoDBQueryExpression<Subscription> queryExpression = new DynamoDBQueryExpression<Subscription>()
	            .withKeyConditionExpression("email = :email and title = :title")
	            .withExpressionAttributeValues(attributeValues);

	    List<Subscription> subscriptions = dynamoDBMapper.query(Subscription.class, queryExpression);

	    // Set the save expression
	    DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
	    if (!subscriptions.isEmpty()) {
	        Map<String, ExpectedAttributeValue> expectedAttributes = new HashMap<>();
	        expectedAttributes.put("email", new ExpectedAttributeValue(new AttributeValue(subscription.getEmail())));
	        expectedAttributes.put("title", new ExpectedAttributeValue(new AttributeValue(subscription.getTitle())));
	        saveExpression.setExpected(expectedAttributes);
	    }

	    // Save the subscription
	    dynamoDBMapper.save(subscription, saveExpression);
	}
	
	
		
	

	    
	
	
}