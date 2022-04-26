package com.viseo.apph.service;

import com.viseo.apph.exception.InvalidFileException;
import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class S3ServiceTest {
    @Mock
    S3Client s3Client;

    @InjectMocks
    S3Service s3Service;

    @Before
    public void init() {
        s3Service = new S3Service(s3Client);
    }

    @Test
    public void testSave() throws InvalidFileException, IOException {
        // Given
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "orig", ContentType.IMAGE_GIF.toString(), "bar".getBytes());
        PutObjectResponse por = PutObjectResponse.builder().eTag("test").build();
        // When
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(por);
        s3Service.save(mockMultipartFile);
        // Then
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    public void testSaveWithNameException() {
        // Given
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        // When
        assertThrows(InvalidFileException.class, () -> s3Service.saveWithName(mockMultipartFile, null));
        assertThrows(InvalidFileException.class, () -> s3Service.saveWithName(null, null));
    }

    @Test
    public void testDownload() {
        // When
        assertThrows(RuntimeException.class, ()->s3Service.download("test"));
        // Then
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()));
    }

    @Test
    public void testDelete() {
        // When
        s3Service.delete("test");
        // Then
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}
