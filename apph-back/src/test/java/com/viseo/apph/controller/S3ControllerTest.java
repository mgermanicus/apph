package com.viseo.apph.controller;

import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.S3Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

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
        s3Controller.upload(file);
        // Then
        verify(s3Service, times(1)).save(any(MockMultipartFile.class));
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
