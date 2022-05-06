package com.viseo.apph.controller;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.dto.PhotoRequest;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.S3Service;
import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
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
        String title = "Test@";
        String format = ".png";
        PhotoRequest photoRequest = new PhotoRequest().setTitle(title).setFile(file);
        String jws = Jwts.builder().claim("id", 1).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        Photo photo = new Photo();
        // When
        when(photoService.addPhoto(title, format, any())).thenReturn(photo);
        when(photoService.getFormat(file)).thenReturn(format);
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(jws, photoRequest);
        // Then
        verify(photoService, times(1)).addPhoto(any(), any(), any());
        assertEquals(responseEntity.getStatusCode().toString()
                , HttpStatus.OK.toString());
    }

    @Test
    public void testUploadException() throws InvalidFileException, IOException {
        // Given
        String title = "Test@";
        PhotoRequest photoRequest = new PhotoRequest().setTitle(title).setFile(null);
        String jws = Jwts.builder().claim("id", 1).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(photoService.getFormat(any())).thenThrow(new InvalidFileException("error"));
        // When
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(jws, photoRequest);
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), responseEntity.getStatusCode().toString());
    }
}
