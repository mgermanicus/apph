package com.viseo.apph.config;

import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    @Value("${accessKey}")
    String accessKey;

    @Value("${secretKey}")
    String secretKey;

    @Value("${region}")
    String region;

    AwsBasicCredentials awsBasicCredentials;

    @Bean(destroyMethod = "close")
    public S3Client s3() {
        awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .region(Region.of(region)).credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials)).build();
    }
}
