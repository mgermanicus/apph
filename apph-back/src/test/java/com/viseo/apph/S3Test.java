package com.viseo.apph;

import com.viseo.apph.controller.S3Controller;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.S3Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class S3Test {
    @Mock
    S3Client s3Client;

    @Mock
    ResponseBytes<GetObjectResponse> s3Object;

    S3Controller s3Controller;
    S3Service s3Service;

    private void createUserController() {
        S3Dao s3Dao = new S3Dao();
        inject(s3Dao, "s3Client", s3Client);
        s3Service = new S3Service();
        inject(s3Service, "s3Dao", s3Dao);
        s3Controller = new S3Controller();
        inject(s3Controller, "s3s", s3Service);
    }

    void inject(Object component, String field, Object injected) {
        try {
            Field compField = component.getClass().getDeclaredField(field);
            compField.setAccessible(true);
            compField.set(component, injected);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpload() {
        // Given
        createUserController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        PutObjectResponse por = PutObjectResponse.builder().eTag("test").build();
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(por);
        // When
        ResponseEntity<IResponseDTO> responseEntity = s3Controller.upload(file);
        // Then
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testUploadException() {
        // Given
        createUserController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        PutObjectResponse por = PutObjectResponse.builder().eTag("test").build();
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(por);
        // When
        ResponseEntity<IResponseDTO> responseEntity = s3Controller.upload(null);
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertThrows(InvalidFileException.class, () -> s3Service.saveWithName(file, null));
        assertThrows(InvalidFileException.class, () -> s3Service.saveWithName(null, null));
    }

    @Test
    public void testDownload() {
        // Given
        createUserController();
        String fileName = "test";
        when(s3Client.getObject(
                GetObjectRequest.builder().bucket(any()).key(any()).build(),
                ResponseTransformer.toBytes())).thenReturn(s3Object);
        // When
        s3Controller.download(fileName);
        // Then
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()));
        verify(s3Object, times(1)).asByteArray();
    }

    @Test
    public void testDelete() {
        // Given
        createUserController();
        String fileName = "test";
        // When
        s3Controller.delete(fileName);
        // Then
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}