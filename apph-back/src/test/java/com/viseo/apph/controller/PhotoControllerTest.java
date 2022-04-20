package com.viseo.apph.controller;

import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.S3Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PhotoControllerTest {
    @Mock
    PhotoService photoService;

    @Mock
    S3Service s3Service;

    @InjectMocks
    PhotoController photoController;

    @Test
    public void testUpload() {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        String name = "Test@";
        // When
        photoController.upload(file,name);
        // Then
        verify(photoService, times(1)).addPhoto(any());
        verify(s3Service, times(1)).save(any());
    }
}
