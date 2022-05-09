package com.viseo.apph;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.controller.PhotoController;
import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.dto.PhotoRequest;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.service.PhotoService;
import io.jsonwebtoken.Jwts;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.Date;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PhotoTest {

    @Mock
    EntityManager em;

    @Mock
    S3Dao s3Dao;

    PhotoService photoService;
    PhotoController photoController;

    private void createPhotoController() {
        PhotoDao photoDao = new PhotoDao();
        inject(photoDao, "em", em);
        photoService = new PhotoService();
        inject(photoService, "s3Dao", s3Dao);
        inject(photoService, "photoDao", photoDao);
        photoController = new PhotoController();
        inject(photoController, "photoService", photoService);
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
    public void testDownload() {
        // GIVEN
        createPhotoController();
        long id = 1L;
        long idUser = 2;
        String title = "test";
        String extension = "jpg";
        byte[] fileByteArray = "".getBytes();
        Photo photo = (Photo) new Photo().setExtension(extension).setTitle(title).setIdUser(idUser).setId(id);
        PhotoRequest photoRequest = new PhotoRequest().setId(id);
        String token = Jwts.builder().claim("id", idUser).setExpiration(
                new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        //WHEN
        when(em.find(any(), anyLong())).thenReturn(photo);
        when(s3Dao.download(anyString())).thenReturn(fileByteArray);
        ResponseEntity<IResponseDTO> responseEntity = photoController.download(token, photoRequest);
        // Then
        verify(em, times(1)).find(Photo.class, id);
        //verify(s3Dao, times(1)).download(id + "");
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        PhotoResponse photoResponse = (PhotoResponse) responseEntity.getBody();
        assert photoResponse != null;
        Assert.assertEquals(title, photoResponse.getTitle());
        Assert.assertEquals(extension, photoResponse.getExtension());
    }

    @Test
    public void testDownloadServerNotWork() {
        // GIVEN
        createPhotoController();
        long id = 1L;
        int idUser = 200;
        Photo photo = (Photo) new Photo().setExtension("png").setTitle("test").setIdUser(idUser).setId(id);
        PhotoRequest photoRequest = new PhotoRequest().setId(id);
        String token = Jwts.builder().claim("id", idUser).setExpiration(
                new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();

        //WHEN
        when(em.find(Photo.class, id)).thenReturn(photo);
        doThrow(S3Exception.class).when(s3Dao).download(anyString());
        ResponseEntity<IResponseDTO> responseEntity = photoController.download(token, photoRequest);
        // Then
        Assert.assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testDownloadUserNotAllowed() {
        // GIVEN
        createPhotoController();
        long id = 1L;
        int idUser = 2;
        Photo photo = (Photo) new Photo().setExtension("png").setTitle("test").setIdUser(idUser).setId(id);
        PhotoRequest photoRequest = new PhotoRequest().setId(id);
        String token = Jwts.builder().claim("id", 1).setExpiration(
                new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        //WHEN
        when(em.find(Photo.class, id)).thenReturn(photo);
        ResponseEntity<IResponseDTO> responseEntity = photoController.download(token, photoRequest);
        // Then
        Assert.assertTrue(responseEntity.getStatusCode().isError());
    }
}
