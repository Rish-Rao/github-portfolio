package com.example.cloudcomputingA1.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.cloudcomputingA1.entity.Music;

@Repository
public class MusicRepository {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public List<Music> findAll() {
        return dynamoDBMapper.scan(Music.class, new DynamoDBScanExpression());
    }
    
    public List<Music> findByTitleAndArtistAndYear(String title, String artist, String year) {

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":title", new AttributeValue().withS(title));
        eav.put(":artist", new AttributeValue().withS(artist));
        eav.put(":yr", new AttributeValue().withS(year));

        DynamoDBQueryExpression<Music> queryExpression = new DynamoDBQueryExpression<Music>()
            .withKeyConditionExpression("title = :title and artist = :artist")
            .withFilterExpression("#yr = :yr")
            .withExpressionAttributeNames(new HashMap<String, String>() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                { put("#yr", "year"); }
            })
            .withExpressionAttributeValues(eav);

        return dynamoDBMapper.query(Music.class, queryExpression);
    }
    
    public List<Music> findByTitle(String title) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val", new AttributeValue().withS(title));

        DynamoDBQueryExpression<Music> queryExpression = new DynamoDBQueryExpression<Music>()
            .withKeyConditionExpression("title = :val")
            .withExpressionAttributeValues(eav);

        return dynamoDBMapper.query(Music.class, queryExpression);
    }

    public List<Music> findByArtist(String artist) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val", new AttributeValue().withS(artist));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("artist = :val")
            .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(Music.class, scanExpression);
    }

    public List<Music> findByYear(String year) {
    	
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val", new AttributeValue().withS(year));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("#yr = :val")
            .withExpressionAttributeNames(new HashMap<String, String>() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				{ put("#yr", "year"); }
            })
            .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(Music.class, scanExpression);
    }
    
    public List<Music> findByTitleAndArtist(String title, String artist) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":title", new AttributeValue().withS(title));
        eav.put(":artist", new AttributeValue().withS(artist));

        DynamoDBQueryExpression<Music> queryExpression = new DynamoDBQueryExpression<Music>()
            .withKeyConditionExpression("title = :title and artist = :artist")
            .withExpressionAttributeValues(eav);

        return dynamoDBMapper.query(Music.class, queryExpression);
    }
    
    public List<Music> findByTitleAndYear(String title, String year) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":title", new AttributeValue().withS(title));
        eav.put(":yr", new AttributeValue().withN(year.toString()));

        DynamoDBQueryExpression<Music> queryExpression = new DynamoDBQueryExpression<Music>()
            .withKeyConditionExpression("title = :title")
            .withFilterExpression("#yr = :yr")
            .withExpressionAttributeNames(new HashMap<String, String>() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                { put("#yr", "year"); }
            })
            .withExpressionAttributeValues(eav);

        return dynamoDBMapper.query(Music.class, queryExpression);
    }
    
    public List<Music> findByArtistAndYear(String artist, String year) {
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":artist", new AttributeValue().withS(artist));
        eav.put(":yr", new AttributeValue().withN(year.toString()));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("artist = :artist and #yr = :yr")
            .withExpressionAttributeNames(new HashMap<String, String>() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                { put("#yr", "year"); }
            })
            .withExpressionAttributeValues(eav);

        return dynamoDBMapper.scan(Music.class, scanExpression);
    }





    public void save(Music music) {
        dynamoDBMapper.save(music);
    }

    public void delete(Music music) {
        dynamoDBMapper.delete(music);
    }
}
