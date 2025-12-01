package com.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

@Service
public class S3Service {

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.access_key_id}")
    private String accessKey;

    @Value("${aws.secret_access_key}")
    private String secretKey;

    public String uploadFile(File file) {

        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);

        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build();

        String key = "product-images/" + file.getName();

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                //.acl("public-read")
                .build();

        s3.putObject(req, file.toPath());

        return "https://" + bucket + ".s3.amazonaws.com/" + key;
    }
}
