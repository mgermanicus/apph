package com.viseo.apph.controller;

import com.viseo.apph.service.PhotoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PhotoControllerTest {
    @Mock
    PhotoService photoService;
    @InjectMocks
    PhotoController photoController = new PhotoController();

    @Test
    public void testUpload() throws IOException {
        //Given
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        //When
        photoController.upload(file);
        //Then
        verify(photoService, times(1)).addPhoto(any());
    }
}
