package com.example.cloudcomputingA1.Interface;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class IntegerToStringConverter implements DynamoDBTypeConverter<String, Integer> {

    @Override
    public String convert(Integer number) {
        return String.valueOf(number);
    }

    @Override
    public Integer unconvert(String string) {
        return Integer.valueOf(string);
    }
}