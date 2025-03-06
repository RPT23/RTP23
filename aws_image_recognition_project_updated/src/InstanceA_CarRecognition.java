package com.example;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.rekognition.AmazonRekognition;
import software.amazon.awssdk.services.rekognition.model.*;

import software.amazon.awssdk.regions.Region;

public class InstanceA_CarRecognition {
    public static void main(String[] args) {
        String bucketName = "njit-cs-643";
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/616826663394/Images";
        S3Client s3 = S3Client.builder().region(Region.US_EAST_1).build();
        SqsClient sqs = SqsClient.builder().region(Region.US_EAST_1).build();
        AmazonRekognition rekognition = AmazonRekognitionClient.builder().region(Region.US_EAST_1).build();
        
        for (int i = 1; i <= 10; i++) {
            String fileName = i + ".jpg";
            DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image().withS3Object(new S3Object().withBucket(bucketName).withName(fileName)))
                .withMinConfidence(90F);
            DetectLabelsResult result = rekognition.detectLabels(request);
            boolean carDetected = result.getLabels().stream().anyMatch(label -> label.getName().equals("Car"));
            if (carDetected) {
                SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .messageBody(fileName)
                        .build();
                sqs.sendMessage(sendMessageRequest);
            }
        }
        // Send termination signal
        sqs.sendMessage(SendMessageRequest.builder().queueUrl(queueUrl).messageBody("-1").build());
        System.out.println("Instance A finished processing.");
    }
}
