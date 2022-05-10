package com.viseo.apph;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.controller.PhotoController;
import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.dao.UserDAO;
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
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PhotoTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery<Photo> typedQuery;

    S3Client s3Client;

    PhotoService photoService;
    PhotoController photoController;


    private void createPhotoController() {
        PhotoDao photoDao = new PhotoDao();
        UserDAO userDAO = new UserDAO();
        inject(photoDao, "em", em);
        inject(userDAO, "em", em);
        S3Dao s3Dao = new S3Dao();
        s3Client = mock(S3Client.class, RETURNS_DEEP_STUBS);
        inject(s3Dao, "s3Client", s3Client);
        photoService = new PhotoService();
        inject(photoService, "photoDao", photoDao);
        inject(photoService, "s3Dao", s3Dao);
        inject(photoService, "userDAO", userDAO);
        photoController = new PhotoController();
        inject(photoController, "photoService", photoService);
    }

    void inject(Object component,String field, Object injected) {
        try {
            Field compField = component.getClass().getDeclaredField(field);
            compField.setAccessible(true);
            compField.set(component,injected);
        }
        catch(IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestGetUserPhotosUrl() {
        //GIVEN
        createPhotoController();
        String token = Jwts.builder().claim("id", 1L).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());
        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user", Photo.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("user"), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(listPhoto);
        when(s3Client.utilities().getUrl((Consumer<GetUrlRequest.Builder>) any()).toExternalForm()).thenReturn("testUrl");
        //WHEN
        List<PhotoResponse> result =  photoController.getUserPhotos(token).getBody();
        //THEN
        assert(Objects.equals(Objects.requireNonNull(result).get(0).getUrl(), "testUrl"));
    }


    @Test
    public void testGetInfos()
    {
        //GIVEN
        createPhotoController();
        String token = Jwts.builder().claim("id", 1L).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());
        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user", Photo.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("user"), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(listPhoto);
        //WHEN
        ResponseEntity<List<PhotoResponse>> responseEntity = photoController.getUserPhotos(token);
        //THEN
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
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
        Photo photo = (Photo) new Photo().setFormat(extension).setTitle(title).setIdUser(idUser).setId(id);
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
        Assert.assertEquals(extension, photoResponse.getFormat());
    }

    @Test
    public void testDownloadUserNotAllowed() {
        // GIVEN
        createPhotoController();
        long id = 1L;
        int idUser = 2;
        Photo photo = (Photo) new Photo().setFormat("png").setTitle("test").setIdUser(idUser).setId(id);
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
