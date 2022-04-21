package com.viseo.apph.controller;

import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.S3Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.Assert.assertThrows;
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
    public void testUpload() throws InvalidFileException, IOException {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        String name = "Test@";
        // When
        assertThrows(RuntimeException.class,
                () -> photoController.upload(file,name));
        // Then
        verify(photoService, times(1)).addPhoto(any());
    }
}
