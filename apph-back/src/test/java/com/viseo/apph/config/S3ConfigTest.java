package com.viseo.apph.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class S3ConfigTest {

    S3Config s3Config;

    public void createS3Config() {
        s3Config = new S3Config();
        s3Config.accessKey = "testK@y";
        s3Config.secretKey = "s@Cret";
        s3Config.region = "us-east-1";
    }

    @Test
    public void testS3Client() {
        // Given
        createS3Config();
        //Then
        try (MockedStatic<S3Client> s3ClientMockedStatic = Mockito.mockStatic(S3Client.class)) {
            assertThrows(RuntimeException.class, () -> s3Config.s3());
            s3ClientMockedStatic.verify(S3Client::builder
                    , times(1));
        }
    }
}
