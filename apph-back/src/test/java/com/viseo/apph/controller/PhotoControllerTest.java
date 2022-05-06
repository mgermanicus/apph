package com.viseo.apph.controller;

import com.viseo.apph.domain.Photo;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.PhotoService;
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
public class PhotoControllerTest {
    @Mock
    PhotoService photoService;

    @InjectMocks
    PhotoController photoController;

    @Test
    public void testUpload() throws InvalidFileException, IOException {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        String name = "Test@";
        Photo photo = new Photo();
        // When
        when(photoService.addPhoto(name)).thenReturn(photo);
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(file, name);
        // Then
        verify(photoService, times(1)).addPhoto(any());
        assertEquals(responseEntity.getStatusCode().toString()
                , HttpStatus.OK.toString());
    }

    @Test
    public void testUploadException() throws InvalidFileException, IOException {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        String name = "Test@";
        Photo photo = new Photo();
        when(photoService.getFormat(any())).thenThrow(new InvalidFileException("error"));
        // When
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(null, name);
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), responseEntity.getStatusCode().toString());
    }
}
