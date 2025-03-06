package com.example.awsimageprocessing;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class InstanceB_TextRecognition {
    public static void main(String[] args) {
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/616826663394/Images";
        String bucketName = "njit-cs-643";
        Region region = Region.US_EAST_1;

     
        SqsClient sqs = SqsClient.builder().region(region).build();
        RekognitionClient rekognition = RekognitionClient.builder().region(region).build();
        S3Client s3 = S3Client.builder().region(region).build();

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
                    DetectTextRequest textRequest = DetectTextRequest.builder()
                            .image(Image.builder().s3Object(S3Object.builder().bucket(bucketName).name(imageName).build()).build())
                            .build();
                    DetectTextResponse textResponse = rekognition.detectText(textRequest);
                    String detectedText = textResponse.textDetections().stream()
                            .map(TextDetection::detectedText)
                            .reduce("", (a, b) -> a + " " + b);
                    writer.write(imageName + ": " + detectedText + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Instance B finished processing.");
    }
}