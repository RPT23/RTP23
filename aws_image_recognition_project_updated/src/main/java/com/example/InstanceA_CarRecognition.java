package com.example;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class InstanceA_CarRecognition {
    public static void main(String[] args) {
        String bucketName = "njit-cs-643";
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/616826663394/Images";

        // Create AWS Clients
        S3Client s3 = S3Client.builder().region(Region.US_EAST_1).build();
        SqsClient sqs = SqsClient.builder().region(Region.US_EAST_1).build();
        RekognitionClient rekognition = RekognitionClient.builder().region(Region.US_EAST_1).build();

        for (int i = 1; i <= 10; i++) {
            String fileName = i + ".jpg";

            // Create request to detect labels
            DetectLabelsRequest request = DetectLabelsRequest.builder()
                .image(Image.builder()
                    .s3Object(software.amazon.awssdk.services.rekognition.model.S3Object.builder()
                        .bucket(bucketName)
                        .name(fileName)
                        .build())
                    .build())
                .minConfidence(90F)
                .build();

            // Call Rekognition to detect labels
            DetectLabelsResponse result = rekognition.detectLabels(request);
            boolean carDetected = result.labels().stream().anyMatch(label -> label.name().equals("Car"));

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
