# AWS Image Recognition Pipeline

## Overview
This project utilizes AWS EC2, S3, SQS, and Rekognition for automated image processing. Two EC2 instances operate in parallel: 
- **Instance A** detects cars in images.
- **Instance B** extracts text from images.

## Prerequisites
Before proceeding, ensure you have the following:
- An **AWS account** with necessary permissions.
- AWS CLI installed and configured (`aws configure`).
- Java 1.8 and Maven installed on EC2 instances.

---
## Setup Instructions
### 1. Create AWS Resources
#### **Create an S3 Bucket**
1. Open the AWS Management Console.
2. Navigate to **S3**.
3. Create a new bucket named **`njit-cs-643`**.
4. Upload images (`1.jpg` to `10.jpg`).

#### **Create an SQS FIFO Queue**
1. Navigate to **SQS**.
2. Create a **FIFO Queue** named `ImageQueue.fifo`.
3. Note the **Queue URL** for later use.

#### **Launch Two EC2 Instances**
1. Go to **EC2** in the AWS Management Console.
2. Launch two instances using **Amazon Linux 2 AMI**.
3. Attach IAM roles with S3, SQS, and Rekognition permissions.
4. Connect to each instance via SSH.

---
### 2. Install Dependencies on EC2 Instances
Run the following commands on both instances:
```bash
sudo yum update -y
sudo yum install java-1.8.0-amazon-corretto-devel maven -y
```

---
### 3. Upload and Run Java Code on EC2 Instances
#### **Instance A - Car Recognition**
1. Upload `InstanceA_CarRecognition.java` and `aws-java-sdk.jar`.
2. Compile and run:
```bash
javac -cp .:aws-java-sdk.jar InstanceA_CarRecognition.java
java -cp .:aws-java-sdk.jar InstanceA_CarRecognition
```

#### **Instance B - Text Recognition**
1. Upload `InstanceB_TextRecognition.java` and `aws-java-sdk.jar`.
2. Compile and run:
```bash
javac -cp .:aws-java-sdk.jar InstanceB_TextRecognition.java
java -cp .:aws-java-sdk.jar InstanceB_TextRecognition
```

#### **Running InstanceA from InstanceB**
Before running `InstanceA` from `InstanceB`, check the value in `pom.xml` using:
```bash
vi pom.xml
```
Ensure that all necessary configurations are correct. Add the following line inside `<build>` in `pom.xml`:
```xml
<mainClass>com.example.InstanceA_CarRecognition</mainClass>
```

---
### 4. Check Output
On **Instance B**, view the extracted text:
```bash
cat output.txt
```

---
## Cleanup
To remove all AWS resources:

### **Terminate EC2 Instances**
```bash
aws ec2 terminate-instances --instance-ids <instance-id>
```

### **Delete S3 Bucket**
```bash
aws s3 rm s3://njit-cs-643 --recursive
```

### **Delete SQS Queue**
```bash
aws sqs delete-queue --queue-url <SQS_QUEUE_URL>
```

---
## Notes
- Ensure **IAM roles** allow access to S3, SQS, and Rekognition.
- Modify **security groups** to allow SSH access (`port 22`).
- AWS billing applies for resources used in this project.

---
## Author
NJIT CS-643
