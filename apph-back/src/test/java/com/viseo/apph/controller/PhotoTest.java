package com.viseo.apph.controller;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.service.PhotoService;
import io.jsonwebtoken.Jwts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PhotoTest {

    PhotoController photoController;
    @Mock
    EntityManager em;
    @Mock
    TokenManager tokenManager;
    @Mock
    TypedQuery typedQuery;

    private void createPhotoController() {
        PhotoDao photoDao = new PhotoDao();
        inject(photoDao, "em", em);
        PhotoService photoService = new PhotoService();
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
    public void testGetInfos() {
        //GIVEN
        createPhotoController();
        String token = Jwts.builder().claim("id", 1L).setExpiration(
                new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());

        when(em.createQuery("SELECT p FROM Photo p WHERE p.idUser=:idUser", Photo.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("idUser", 1L)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(listPhoto);
        when(tokenManager.getIdOfToken("token")).thenReturn(1);
        //WHEN
        ResponseEntity responseEntity = photoController.getUserPhotos(token);
        //THEN
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

}
