package com.viseo.apph.controller;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.PhotoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

import static com.viseo.apph.utils.Utils.inject;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PhotoTest {

PhotoController photoController;
@Mock
EntityManager em ;

@Mock
Utils utils;

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


    @Test
    public void testGetInfos() {
        //GIVEN
        createPhotoController();
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());
        PhotoController.utils = utils;
        when(em.createQuery("SELECT p FROM Photo p WHERE p.idUser=:idUser", Photo.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("idUser", 1L)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(listPhoto);
        when(utils.getUser()).thenReturn((User)new User().setId(1));
        //WHEN
        ResponseEntity responseEntity = photoController.getUserPhotos();
        //THEN
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }
}
