package com.viseo.apph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.controller.PhotoController;
import com.viseo.apph.dao.*;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.*;
import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.TagService;
import com.viseo.apph.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.security.Key;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PhotoTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery<Photo> typedQueryPhoto;
    @Mock
    TypedQuery<User> typedQueryUser;
    @Mock
    S3Dao s3Dao;

    S3Client s3Client;
    UserService userService;
    TagService tagService;
    PhotoService photoService;
    PhotoController photoController;
    PhotoRequest photoRequest;

    private void createPhotoController() {
        PhotoDao photoDao = new PhotoDao();
        UserDao userDao = new UserDao();
        TagDao tagDao = new TagDao();
        FolderDao folderDao = new FolderDao();
        inject(photoDao, "em", em);
        inject(userDao, "em", em);
        inject(tagDao, "em", em);
        inject(folderDao, "em", em);
        S3Dao s3Dao = new S3Dao();
        s3Client = mock(S3Client.class, RETURNS_DEEP_STUBS);
        inject(s3Dao, "s3Client", s3Client);
        photoService = new PhotoService();
        inject(photoService, "photoDao", photoDao);
        inject(photoService, "s3Dao", s3Dao);
        inject(photoService, "userDao", userDao);
        userService = new UserService();
        inject(userService, "userDao", userDao);
        inject(userService, "folderDao", folderDao);
        tagService = new TagService();
        inject(tagService, "tagDao", tagDao);
        inject(tagService, "userDao", userDao);
        photoController = new PhotoController();
        inject(photoController, "photoService", photoService);
        inject(photoController, "userService", userService);
        inject(photoService, "tagService", tagService);
    }

    private void createPhotoControllerWithoutS3() {
        PhotoDao photoDao = new PhotoDao();
        UserDao userDao = new UserDao();
        inject(photoDao, "em", em);
        inject(userDao, "em", em);
        photoService = new PhotoService();
        inject(photoService, "photoDao", photoDao);
        inject(photoService, "s3Dao", s3Dao);
        inject(photoService, "userDao", userDao);
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
    public void TestUploadPhoto() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", "bar".getBytes());
        Tag tag = new Tag().setName("+ Add New Tag totoTestTag");
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        photoRequest = new PhotoRequest().setTitle("totoPhoto").setFile(file).setTags(gson.toJson(tags));
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", user.getLogin())).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(user);
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(jws, photoRequest);
        //THEN
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void TestGetUserPhotosUrl() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        String token = Jwts.builder().claim("login", robert.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());
        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Robert")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(robert);
        when(s3Client.utilities().getUrl((Consumer<GetUrlRequest.Builder>) any()).toExternalForm()).thenReturn("testUrl");
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.getUserPhotos(token, 5, 1);
        //THEN
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        PaginationResponse paginationResponse = (PaginationResponse) responseEntity.getBody();
        assert (Objects.equals(Objects.requireNonNull(Objects.requireNonNull(paginationResponse).getPhotoList()).get(0).getUrl(), "testUrl"));
    }

    @Test
    public void testGetInfos() {
        //GIVEN
        createPhotoController();
        List<Photo> listPhoto = new ArrayList<>();
        Date creationDate = new Date();
        Date shootingDate = new Date();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Tag tag = new Tag().setUser(robert).setName("robertTag");
        listPhoto.add((Photo) new Photo().setSize(10).setTitle("photo 1").setCreationDate(creationDate).setShootingDate(shootingDate).setDescription("description").addTag(tag).setId(1L));
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        String token = Jwts.builder().claim("login", robert.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Robert")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(robert);
        when(s3Client.utilities().getUrl((Consumer<GetUrlRequest.Builder>) any()).toExternalForm()).thenReturn("testUrl");
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.getUserPhotos(token, 5, 1);
        //THEN
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        PaginationResponse paginationResponse = (PaginationResponse) responseEntity.getBody();
        assert paginationResponse != null;
        Assert.assertEquals(6, paginationResponse.getTotalSize());
        Assert.assertEquals(5, paginationResponse.getPhotoList().size());
        PhotoResponse photo = paginationResponse.getPhotoList().get(0);
        Assert.assertEquals(10, photo.getSize(), 0.0f);
        Assert.assertEquals("photo 1", photo.getTitle());
        Assert.assertEquals(creationDate, photo.getCreationDate());
        Assert.assertEquals(shootingDate, photo.getShootingDate());
        Assert.assertEquals("description", photo.getDescription());
        Assert.assertTrue(photo.getTags().contains(tag));
        Assert.assertEquals(1, photo.getId());
        Assert.assertEquals("testUrl", photo.getUrl());
    }

    @Test
    public void testGetInfosWithNotExistingUser() {
        //GIVEN
        createPhotoController();
        String token = Jwts.builder().claim("login", "Robert").setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Robert")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenThrow(new NoResultException());
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.getUserPhotos(token, 5, 1);
        //THEN
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("L'utilisateur n'existe pas.", messageResponse.getMessage());
    }

    @Test
    public void testGetInfosWithIllegalArgument() {
        //GIVEN
        createPhotoController();
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        String token = Jwts.builder().claim("login", robert.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Robert")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(robert);
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.getUserPhotos(token, 5, -1);
        //THEN
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("Argument illégal.", messageResponse.getMessage());
    }

    @Test
    public void testDownload() {
        // GIVEN
        createPhotoControllerWithoutS3();
        long id = 1L;
        long idUser = 2;
        String title = "test";
        String extension = "jpg";
        byte[] fileByteArray = "".getBytes();
        User user = (User) new User().setId(idUser);
        Photo photo = (Photo) new Photo().setFormat(extension).setTitle(title).setUser(user).setId(id);
        PhotoRequest photoRequest = new PhotoRequest().setId(id);
        String token = Jwts.builder().claim("id", idUser).setExpiration(
                new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        //WHEN
        when(em.find(any(), anyLong())).thenReturn(photo);
        when(s3Dao.download(any())).thenReturn(fileByteArray);
        ResponseEntity<IResponseDTO> responseEntity = photoController.download(token, photoRequest);
        // Then
        verify(em, times(1)).find(Photo.class, id);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        PhotoResponse photoResponse = (PhotoResponse) responseEntity.getBody();
        assert photoResponse != null;
        Assert.assertEquals(title, photoResponse.getTitle());
        Assert.assertEquals(extension, photoResponse.getFormat());
    }

    @Test
    public void testDownloadUserNotAllowed() {
        // GIVEN
        createPhotoController();
        long id = 1L;
        int idUser = 2;
        User user = (User) new User().setId(idUser);
        Photo photo = (Photo) new Photo().setFormat("png").setTitle("test").setUser(user).setId(id);
        PhotoRequest photoRequest = new PhotoRequest().setId(id);
        String token = Jwts.builder().claim("id", 1).setExpiration(
                new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        //WHEN
        when(em.find(Photo.class, id)).thenReturn(photo);
        ResponseEntity<IResponseDTO> responseEntity = photoController.download(token, photoRequest);
        // Then
        Assert.assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testFailUserNotFind() {
        //GIVEN
        createPhotoController();
        String jws = Jwts.builder().claim("login", "dumb_toto").setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        //WHEN
        ResponseEntity responseEntity = photoController.getUserPhotos(jws, 1, 1);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void testFailWrongSignature() {
        //GIVEN
        createPhotoController();
        User user = new User().setLogin("toto").setPassword("password");
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis())).signWith(key).compact();
        //WHEN
        ResponseEntity responseEntity = photoController.getUserPhotos(jws, 1, 1);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testFailTokenExpired() {
        //GIVEN
        createPhotoController();
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis())).signWith(JwtConfig.getKey()).compact();
        //WHEN
        ResponseEntity responseEntity = photoController.getUserPhotos(jws, 1, 1);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testFailInvalidFormat() {
        //GIVEN
        createPhotoController();
        MockMultipartFile failFile = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        Set<Tag> tags = new HashSet<>();
        Gson gson = new GsonBuilder().create();
        photoRequest = new PhotoRequest().setTitle("totoPhoto").setFile(failFile).setTags(gson.toJson(tags));
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", user.getLogin())).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(user);
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(jws, photoRequest);
        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}
