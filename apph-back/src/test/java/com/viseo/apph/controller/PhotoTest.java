package com.viseo.apph.controller;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import com.viseo.apph.service.PhotoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PhotoTest {

PhotoController photoController;
@Mock
EntityManager em ;
@Mock
TokenManager tokenManager;
@Mock
TypedQuery typedQuery;
@Mock
UserDAO userDAO;

    private void createPhotoController() {
        PhotoDao photoDao = new PhotoDao();
        inject(photoDao,"em",em);
        PhotoService photoService = new PhotoService();
        inject(photoService,"photoDao",photoDao);
        inject(photoService, "userDAO", userDAO);
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
        PhotoController.tokenManager = tokenManager;

        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user", Photo.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("user", new User())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(listPhoto);
        when(tokenManager.getIdOfToken("token")).thenReturn(1);
        when(userDAO.getUserById(1)).thenReturn(new User());
        //WHEN
        ResponseEntity responseEntity = photoController.getUserPhotos(token);
        //THEN
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

}
