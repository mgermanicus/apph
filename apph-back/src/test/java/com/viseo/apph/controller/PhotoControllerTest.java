package com.viseo.apph.controller;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.dto.PhotoRequest;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.PhotoService;
import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PhotoControllerTest {
    @Mock
    PhotoService photoService;

    @InjectMocks
    PhotoController photoController;

    @Test
    public void testUpload() {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        String title = "Test@";
        PhotoRequest photoRequest = new PhotoRequest().setTitle(title).setFile(file);
        String jws = Jwts.builder().claim("id", 1).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        Photo photo = (Photo) new Photo().setId(1L);
        // When
        when(photoService.addPhoto(any())).thenReturn(photo);
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(jws, photoRequest);
        // Then
        verify(photoService, times(1)).addPhoto(any());
        assertEquals(responseEntity.getStatusCode().toString(), HttpStatus.OK.toString());
    }

    @Test
    public void testUploadException() throws InvalidFileException {
        // Given
        String title = "Test@";
        PhotoRequest photoRequest = new PhotoRequest().setTitle(title).setFile(null);
        String jws = Jwts.builder().claim("id", 1).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(photoService.getPhotoByRequest(photoRequest, 1L)).thenThrow(new InvalidFileException("error"));
        // When
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(jws, photoRequest);
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), responseEntity.getStatusCode().toString());
    }
}
