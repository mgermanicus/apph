package com.viseo.apph.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.dto.PhotoRequest;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.PhotoService;
import io.jsonwebtoken.Jwts;
import com.viseo.apph.service.TagService;
import com.viseo.apph.service.UserService;
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
import java.util.HashSet;
import java.util.Set;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PhotoControllerTest {
    @Mock
    PhotoService photoService;
    @Mock
    TagService tagService;
    @Mock
    UserService userService;

    @InjectMocks
    PhotoController photoController;

    @Test
    public void testUpload() throws IOException {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        String title = "Test@";
        String format = ".png";
        PhotoRequest photoRequest = new PhotoRequest().setTitle(title).setFile(file);
        String jws = Jwts.builder().claim("id", 1).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        Photo photo = (Photo) new Photo().setId(1L);
        String name = "Test@";
        Set<Tag> tags = new HashSet<>();
        Photo photo = new Photo().setTags(tags);
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        // When
        when(photoService.addPhoto(title, format, 1)).thenReturn(photo);
        when(photoService.getFormat(file)).thenReturn(format);
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(jws, photoRequest);
        when(photoService.addPhoto(name, tags)).thenReturn(photo);
        when(userService.getUser(any())).thenReturn(user);
        when(tagService.createListTags(gson.toJson(tags), user)).thenReturn(tags);
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(jws, file, name, gson.toJson(tags));
        // Then
        verify(photoService, times(1)).addPhoto(title, format, 1);
        verify(photoService, times(1)).addPhoto(any(), any());
        assertEquals(responseEntity.getStatusCode().toString()
                , HttpStatus.OK.toString());
    }

    @Test
    public void testUploadException() throws InvalidFileException {
        // Given
        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        String name = "Test@";
        Set<Tag> tags = new HashSet<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
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
