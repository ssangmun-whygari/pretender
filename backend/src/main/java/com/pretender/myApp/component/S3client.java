package com.pretender.myApp.component;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Component
public class S3client {

    private volatile S3Client s3Client;

    public S3Client getS3Client() {
        if (s3Client == null) {
            synchronized (this) {
                if (s3Client == null) {
                    s3Client = S3Client.builder()
                            .region(Region.AP_NORTHEAST_2)
                            .build(); // IAM Role or ~/.aws/credentials 자동 탐색
                    System.out.println("#########getS3Client...");
                    System.out.println(s3Client);
                    System.out.println("#########getS3Client end");
                }
            }
        }
        return s3Client;
    }
}