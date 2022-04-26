package com.viseo.apph.controller;

import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.S3Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class S3ControllerTest {
    @Mock
    S3Service s3Service;

    @InjectMocks
    S3Controller s3Controller;

    @Test
    public void testUpload() throws InvalidFileException, IOException {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        // When
        ResponseEntity<IResponseDTO> responseEntity = s3Controller.upload(file);
        // Then
        verify(s3Service, times(1)).save(any(MockMultipartFile.class));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testUploadException() throws InvalidFileException, IOException {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        // When
        when(s3Service.save(any())).thenThrow(InvalidFileException.class);
        ResponseEntity<IResponseDTO> responseEntity = s3Controller.upload(file);
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void testDownload() {
        // Given
        String fileName = "test";
        // When
        s3Controller.download(fileName);
        // Then
        verify(s3Service, times(1)).download(anyString());
    }

    @Test
    public void testDelete() {
        // Given
        String fileName = "test";
        // When
        s3Controller.delete(fileName);
        // Then
        verify(s3Service, times(1)).delete(anyString());
    }
}
