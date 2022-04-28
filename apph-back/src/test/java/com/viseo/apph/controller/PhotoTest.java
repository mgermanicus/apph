package com.viseo.apph.controller;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PhotoTest {

PhotoController photoController;
@Mock
EntityManager em ;
@Mock
TypedQuery typedQuery;
    private void createPhotoController() {
        PhotoDao photoDao = new PhotoDao();
        inject(photoDao,"em",em);
        PhotoService photoService = new PhotoService();
        inject(photoService,"photoDao",photoDao);
        photoController = new PhotoController();
        inject(photoController,"photoService",photoService);
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
    public void testGetInfos()
    {
        //GIVEN
        createPhotoController();
        String token = "token";
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());
        PhotoController.tokenManager = new PhotoController.TokenManager() {
            @Override
            public int getIdOfToken(String token) {
                return 1;
            }
        };
        when(em.createQuery("SELECT p FROM Photo p WHERE p.idUser=:idUser", Photo.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("idUser", 1L)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(listPhoto);

        //WHEN
        ResponseEntity responseEntity = photoController.getUserPhotos(token);
        //THEN
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

}
