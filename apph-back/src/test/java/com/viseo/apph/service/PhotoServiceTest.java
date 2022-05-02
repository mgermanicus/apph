package com.viseo.apph.service;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.exception.InvalidFileException;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
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
            emField.set(dao, em);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFormat() throws InvalidFileException {
        // Given
        createPhotoService();
        MockMultipartFile fileException = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        MockMultipartFile file = new MockMultipartFile("file", "orig", ContentType.IMAGE_GIF.toString(), "bar".getBytes());
        // When
        assertThrows(InvalidFileException.class, () -> photoService.getFormat(fileException));
        assertEquals("gif",photoService.getFormat(file));
    }
}
