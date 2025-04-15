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
@DynamoDBTable(tableName = "music")
public class Music {

    @DynamoDBHashKey(attributeName = "title")
    private String title;

    @DynamoDBRangeKey(attributeName = "artist")
    private String artist;

    @DynamoDBAttribute(attributeName = "img_url")
    private String imgUrl;
    
    @DynamoDBAttribute(attributeName = "web_url")
    private String webUrl;

    @DynamoDBAttribute(attributeName = "year")
    @DynamoDBTypeConverted(converter = StringToIntegerConverter.class)
    private Integer year;

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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}


	}