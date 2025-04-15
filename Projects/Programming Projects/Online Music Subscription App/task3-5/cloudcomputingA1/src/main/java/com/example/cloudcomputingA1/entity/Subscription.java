package com.example.cloudcomputingA1.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.example.cloudcomputingA1.Interface.StringToIntegerConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "subscription")
public class Subscription {

    @DynamoDBHashKey(attributeName = "email")
    private String email;

    @DynamoDBRangeKey(attributeName = "title")
    private String title;

    @DynamoDBAttribute(attributeName = "artist")
    private String artist;

    @DynamoDBAttribute(attributeName = "year")
    @DynamoDBTypeConverted(converter = StringToIntegerConverter.class)
    private Integer year;

    private String imageUrl;


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}


    // existing constructors and getters/setters
	public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}