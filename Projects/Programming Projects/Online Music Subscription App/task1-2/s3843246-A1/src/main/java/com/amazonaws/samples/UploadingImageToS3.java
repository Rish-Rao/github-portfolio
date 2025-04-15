package com.amazonaws.samples;

import java.io.*;
import java.net.*;
import java.util.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;

public class UploadingImageToS3 {

    public static void main(String[] args) throws Exception {
    	

    	    
        Regions region = Regions.US_EAST_1;
        String bucketName = "s3843246-mybucket";//e.g., sxxxxxxx-s3test

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(region)
                    .build();
        

            if (!s3Client.doesBucketExistV2(bucketName)) {
                // Because the CreateBucketRequest object doesn't specify a region, the
                // bucket is created in the region specified in the client.
                s3Client.createBucket(new CreateBucketRequest(bucketName));

                // Verify that the bucket was created by retrieving it and checking its location.
                String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
                System.out.println("Bucket location: " + bucketLocation);
                
                

                // Download and upload each image
                
            }
            String jsonFilePath = "a1.json";
            List<String> imageURLs = parseImageURLsFromJSON(jsonFilePath);
            for (String s:imageURLs) {
            	System.out.println(s);
            }
            
            for (String imageURL : imageURLs) {
                // Download the image as a byte array
                byte[] imageBytes = downloadImage(imageURL);
                
                // Upload the byte array as an object in the S3 bucket
                String key = getFileNameFromURL(imageURL);
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(imageBytes.length);
                s3Client.putObject(bucketName, key, new ByteArrayInputStream(imageBytes), metadata);
                System.out.println("Uploaded " + key + " to S3 bucket " + bucketName);
            }
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it and returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

        // Load AWS credentials from environment variables
//        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
//
//        // Initialize the S3 client
//        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(region)
//                .build();

        // Parse the JSON file containing image URLs
        
    }

    // Parse the image URLs from the JSON file and return as a list of strings
    private static List<String> parseImageURLsFromJSON(String jsonFilePath) throws Exception {
        List<String> imageURLs = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String imageURL = line.trim();
            if (imageURL.startsWith("\"img")) {
            	imageURL = imageURL.substring(imageURL.indexOf("http"), imageURL.lastIndexOf("\""));
                imageURLs.add(imageURL);
            }
        }
        reader.close();
        return imageURLs;
    }

    // Download the image from the given URL and return as a byte array
    private static byte[] downloadImage(String imageURL) throws Exception {
        URL url = new URL(imageURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        byte[] imageBytes = outputStream.toByteArray();
        outputStream.close();
        inputStream.close();
        return imageBytes;
    }

    // Extract the file name from the given URL
    private static String getFileNameFromURL(String url) {
        String[] tokens = url.split("/");
        return tokens[tokens.length - 1];
    }

}