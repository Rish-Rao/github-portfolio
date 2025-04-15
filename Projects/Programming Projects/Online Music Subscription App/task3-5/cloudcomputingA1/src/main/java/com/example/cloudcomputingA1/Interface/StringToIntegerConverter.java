package com.example.cloudcomputingA1.Interface;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class StringToIntegerConverter implements DynamoDBTypeConverter<String, Integer> {

    @Override
    public String convert(final Integer year) {
        return year == null ? null : year.toString();
    }

    @Override
    public Integer unconvert(final String yearString) {
        return yearString == null ? null : Integer.valueOf(yearString);
    }
}
