// Copyright 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

package com.amazonaws.samples;

import java.io.File;
import java.util.Iterator;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LoadingMusicData {

    public static void main(String[] args) throws Exception {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
        	.withRegion(Regions.US_EAST_1)
            .withCredentials(new ProfileCredentialsProvider("default"))
            .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("music");

        JsonParser parser = new JsonFactory().createParser(new File("a1.json"));

        JsonNode rootNode = new ObjectMapper().readTree(parser);
        Iterator<JsonNode> iter = rootNode.iterator();
        
        
        while (iter.hasNext()) {
        	JsonNode currentNode = iter.next();
        	ArrayNode aNode = (ArrayNode) currentNode;
        	
        	for (JsonNode iNode: aNode) {
        		ObjectNode oNode = (ObjectNode) iNode;
        		String title = oNode.path("title").asText();
                String artist = oNode.path("artist").asText();
    
   
	            try {
	                table.putItem(new Item().withPrimaryKey("title", title, "artist", artist).with("year",
	                		oNode.path("year").asText()).with("web_url", oNode.path("web_url").asText()).with("img_url", oNode.path("img_url").asText()));
	                System.out.println("PutItem succeeded: " + title + " " + artist);
	
	            }
	            catch (Exception e) {
	                System.err.println("Unable to add music: " + title + " " + artist);
	                System.err.println(e.getMessage());
	                break;
	            }
            }
        }
        parser.close();
    }
}