package com.viseo.apph.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public class S3ConfigTest {

    S3Config s3Config;

    public void createS3Config(){
        s3Config = new S3Config();
        s3Config.accessKey = "testK@y";
        s3Config.secretKey = "s@Cret";
        s3Config.region = "r@Gion";
    }
    @Test
    public void testS3Client(){
        // Given
        createS3Config();
        // Then
        assertThrows(RuntimeException.class, ()->s3Config.s3());
    }
}
