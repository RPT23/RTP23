package com.example;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.s3.S3Client;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class InstanceB_TextRecognition {
    public static void main(String[] args) {
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/616826663394/Images";
        String bucketName = "njit-cs-643";
        Region region = Region.US_EAST_1;

        // AWS Clients
        SqsClient sqs = SqsClient.builder().region(region).credentialsProvider(DefaultCredentialsProvider.create()).build();
        RekognitionClient rekognition = RekognitionClient.builder().region(region).credentialsProvider(DefaultCredentialsProvider.create()).build();
        S3Client s3 = S3Client.builder().region(region).credentialsProvider(DefaultCredentialsProvider.create()).build();

        boolean done = false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            while (!done) {
                ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(1)
                        .waitTimeSeconds(10)
                        .build();
                ReceiveMessageResponse response = sqs.receiveMessage(receiveRequest);
                List<Message> messages = response.messages();

                for (Message message : messages) {
                    String imageName = message.body();

                    if (imageName.equals("-1")) {
                        done = true;
                        break;
                    }

                    // Create DetectTextRequest with Correct AWS SDK v2 Syntax
                    DetectTextRequest textRequest = DetectTextRequest.builder()
                            .image(Image.builder()
                                    .s3Object(software.amazon.awssdk.services.rekognition.model.S3Object.builder()
                                            .bucket(bucketName)
                                            .name(imageName)
                                            .build())
                                    .build())
                            .build();

                    // Call AWS Rekognition to Detect Text
                    DetectTextResponse textResponse = rekognition.detectText(textRequest);

                    // Extract detected text
                    String detectedText = textResponse.textDetections().stream()
                            .map(TextDetection::detectedText)
                            .reduce("", (a, b) -> a + " " + b);

                    // Write results to file
                    writer.write(imageName + ": " + detectedText + "\n");
                    System.out.println("Processed Image: " + imageName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Instance B finished processing.");
    }
}
