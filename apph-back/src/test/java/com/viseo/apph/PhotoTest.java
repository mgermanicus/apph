package com.viseo.apph;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.controller.PhotoController;
import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.PaginationRequest;
import com.viseo.apph.dto.PaginationResponse;
import com.viseo.apph.service.PhotoService;
import io.jsonwebtoken.Jwts;
import org.junit.Assert;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.dto.PhotoResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        String token = Jwts.builder().claim("login", robert.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT p FROM Photo p WHERE p.idUser=:idUser", Photo.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter(eq("user"), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(listPhoto);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("login", "Robert")).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(robert);
        //WHEN
        ResponseEntity responseEntity = photoController.getUserPhotos(token, 5, 1);
        //THEN
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        PaginationResponse paginationResponse = (PaginationResponse) responseEntity.getBody();
        Assert.assertEquals(6, paginationResponse.getTotalSize());
        Assert.assertEquals(5, paginationResponse.getPhotoList().size());
    }
}
>>>>>>>> e0e2fe7 ([feature][APPH-13] pagination back side + back test):apph-back/src/test/java/com/viseo/apph/PhotoTest.java
