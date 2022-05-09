package com.viseo.apph.service;

import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import com.viseo.apph.exception.InvalidFileException;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PhotoServiceTest {

    @Mock
    EntityManager em;
    @Mock
    TypedQuery typedQuery;
    PhotoService photoService;

    private void createPhotoService() {
        PhotoDao photoDao = new PhotoDao();
        setEntityManager(photoDao, em);
        UserDao userDao = new UserDao();
        setEntityManager(userDao, em);
        photoService = new PhotoService();
        photoService.photoDao = photoDao;
        inject(photoService, "userDao", userDao);
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
    public void testAddPhoto() {
        // Given
        createPhotoService();
        User user = new User().setLogin("toto");
        String name = "Test@";
        Set<Tag> tags = new HashSet<>();
        String format = ".png";
        // WHEN
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("login", "toto")).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(user);
        photoService.addPhoto(name, format, tags, user.getLogin());
        // THEN
        verify(em, times(1)).persist(any(Photo.class));
    }

    @Test
    public void testGetFormat() throws InvalidFileException {
        // Given
        createPhotoService();
        MockMultipartFile fileException = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        MockMultipartFile file = new MockMultipartFile("file", "orig", ContentType.IMAGE_GIF.toString(), "bar".getBytes());
        // When
        assertThrows(InvalidFileException.class, () -> photoService.getFormat(fileException));
        assertEquals(".gif", photoService.getFormat(file));
    }
}