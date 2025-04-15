package com.example.cloudcomputingA1.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.example.cloudcomputingA1.entity.User;

@Repository
public class UserRepository {
	
	@Autowired
	private DynamoDBMapper dynamoDBMapper;
	
	
	public User save(User user) {
		dynamoDBMapper.save(user);
		return user;
	}
	
	public User getUserByEmail(String email) {
		return dynamoDBMapper.load(User.class, email);
	}
	
	public String delete(String email) {
		User us = dynamoDBMapper.load(User.class, email);
		dynamoDBMapper.delete(us);
		return "User Deleted";
	}
	
	public String update(String email, User user) {
		dynamoDBMapper.save(user,
				new DynamoDBSaveExpression()
				.withExpectedEntry("email",
						new ExpectedAttributeValue(
								new AttributeValue().withS(email)
								)));
		return email;
		
	}
}