# AWS Image Recognition Pipeline

## Overview
This project uses **AWS EC2, S3, SQS, and Rekognition** for image processing. Two EC2 instances work in parallel: one detects cars in images, the other extracts text.

## Setup Instructions
1. **Create AWS Resources:**
   - S3 Bucket (`njit-cs-643`) and upload `1.jpg` - `10.jpg`.
   - SQS FIFO Queue (`ImageQueue.fifo`) and note the **queue URL**.
   - Launch two EC2 instances (Amazon Linux 2).

2. **Install Dependencies on EC2:**
   ```sh
   sudo yum update -y
   sudo yum install java-1.8.0-amazon-corretto-devel maven -y
   ```

3. **Upload and Run Java Code on EC2:**
   ```sh
   javac -cp .:aws-java-sdk.jar InstanceA_CarRecognition.java
   java -cp .:aws-java-sdk.jar InstanceA_CarRecognition
   ```

   ```sh
   javac -cp .:aws-java-sdk.jar InstanceB_TextRecognition.java
   java -cp .:aws-java-sdk.jar InstanceB_TextRecognition
   ```

4. **Check Output on Instance B:**  
   ```sh
   cat output.txt
   ```

## Cleanup
```sh
aws ec2 terminate-instances --instance-ids <instance-id>
aws s3 rm s3://njit-cs-643 --recursive
aws sqs delete-queue --queue-url <SQS_QUEUE_URL>
```
