package com.viseo.apph;

import com.viseo.apph.controller.PhotoController;
import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.dto.PhotoRequest;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.service.PhotoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.model.S3Exception;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PhotoTest {

    @Mock
    EntityManager em;

    @Mock
    S3Dao s3Dao;

    PhotoService photoService;
    PhotoController photoController;

    private void createPhotoController() {
        PhotoDao photoDao = new PhotoDao();
        inject(photoDao, "em", em);
        photoService = new PhotoService();
        inject(photoService, "s3Dao", s3Dao);
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
    public void testDownload() {
        // GIVEN
        createPhotoController();
        long id = 1L;
        String title = "test";
        String extension = "jpg";
        PhotoRequest photoRequest = new PhotoRequest().setId(id);
        byte[] fileByteArray = "".getBytes();
        Photo photo = (Photo) new Photo().setExtension(extension).setTitle(title).setId(id);
        //WHEN
        when(em.find(Photo.class, id)).thenReturn(photo);
        when(s3Dao.download(id + "")).thenReturn(fileByteArray);
        ResponseEntity<IResponseDTO> responseEntity = photoController.download(photoRequest);
        // Then
        verify(em, times(1)).find(Photo.class, id);
        verify(s3Dao, times(1)).download(id + "");
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        PhotoResponse photoResponse = (PhotoResponse) responseEntity.getBody();
        assert photoResponse != null;
        Assert.assertEquals(title, photoResponse.getTitle());
        Assert.assertEquals(extension, photoResponse.getExtension());
    }

    @Test
    public void testDownloadNotExist() {
        // GIVEN
        createPhotoController();
        long id = 1L;
        PhotoRequest photoRequest = new PhotoRequest().setId(id);
        Photo photo = (Photo) new Photo().setExtension("png").setTitle("test").setId(id);
        //WHEN
        when(em.find(Photo.class, id)).thenReturn(photo);
        doThrow(S3Exception.class).when(s3Dao).download(anyString());
        ResponseEntity<IResponseDTO> responseEntity = photoController.download(photoRequest);
        // Then
        Assert.assertTrue(responseEntity.getStatusCode().isError());
    }
}
