package com.viseo.apph.service;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.domain.Photo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PhotoServiceTest {

    @Mock
    EntityManager em;

    PhotoService photoService;

    private void createPhotoService() {
        PhotoDao photoDao = new PhotoDao();
        setEntityManager(photoDao, em);
        photoService = new PhotoService();
        photoService.photoDao = photoDao;
    }

    void setEntityManager(Object dao, EntityManager em) {
        try {
            Field emField = dao.getClass().getDeclaredField("em");
            emField.setAccessible(true);
            emField.set(dao,em);
        }
        catch(IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddPhoto() {
        // Given
        createPhotoService();
        String title = "Test@";
        // WHEN
        photoService.addPhoto(title);
        // THEN
        verify(em, times(1)).persist(any(Photo.class));
    }
}
