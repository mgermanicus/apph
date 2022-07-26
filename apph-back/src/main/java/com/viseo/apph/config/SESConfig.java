package com.viseo.apph.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class SESConfig {
    // we use a different account AWS (Team/email.viseo.com) so new accessKey and secretKey are necessary
    @Value("${newAccessKey}")
    String accessKey;

    @Value("${newSecretKey}")
    String secretKey;

    @Value("${region}")
    String region;

    AwsBasicCredentials awsBasicCredentials;

    @Bean(destroyMethod = "close")
    public SesClient ses() {
        awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return SesClient.builder()
                .region(Region.of(region)).credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials)).build();
    }
}
