package com.viseo.apph;

import com.viseo.apph.controller.PhotoController;
import com.viseo.apph.controller.TokenManager;
import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.service.PhotoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class photoTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery typedQuery;
    @Mock
    TokenManager tokenManager;
    S3Client s3Client;

    PhotoService photoService;
    PhotoController photoController;


    private void createPhotoController() {
        PhotoDao photoDao = new PhotoDao();
        inject(photoDao, "em", em);
        S3Dao s3Dao = new S3Dao();
        s3Client = mock(S3Client.class, RETURNS_DEEP_STUBS);
        inject(s3Dao, "s3Client", s3Client);
        photoService = new PhotoService();
        inject(photoService, "photoDao", photoDao);
        inject(photoService, "s3Dao", s3Dao);
        photoController = new PhotoController();
        inject(photoController, "photoService", photoService);
        inject(photoController, "tokenManager", tokenManager);
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
        String token = "token";
        when(tokenManager.getIdOfToken("token")).thenReturn(1);
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());
        when(em.createQuery("SELECT p FROM Photo p WHERE p.idUser=:idUser", Photo.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("idUser", 1L)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(listPhoto);
        when(s3Client.utilities().getUrl((Consumer<GetUrlRequest.Builder>) any()).toExternalForm()).thenReturn("testUrl");
        //WHEN
        List<PhotoResponse> result =  photoController.getUserPhotos(token).getBody();
        //THEN
        assert(Objects.equals(Objects.requireNonNull(result).get(0).getUrl(), "testUrl"));
    }
}