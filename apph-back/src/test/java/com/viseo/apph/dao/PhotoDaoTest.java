package com.viseo.apph.dao;

import com.viseo.apph.domain.Photo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PhotoDaoTest {

    @Mock
    EntityManager mockEm;

    @InjectMocks
    PhotoDao photoDao = new PhotoDao();

    @Test
    public void testAddPhoto() {
        //Given
        String fileName = "test";
        Photo photo = new Photo().setName(fileName);
        //When
        photoDao.addPhoto(photo);
        //Then
        verify(mockEm, times(1)).persist(any());
    }
}