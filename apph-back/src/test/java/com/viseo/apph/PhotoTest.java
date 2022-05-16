package com.viseo.apph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viseo.apph.controller.PhotoController;
import com.viseo.apph.dao.*;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.*;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.TagService;
import com.viseo.apph.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.function.Consumer;

import static com.viseo.apph.utils.Utils.inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PhotoTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery<Photo> typedQueryPhoto;
    @Mock
    TypedQuery<User> typedQueryUser;
    @Mock
    TypedQuery<Folder> typedQueryFolder;
    @Mock
    Utils utils;
    @Mock
    S3Client s3Client;

    PhotoController photoController;

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
        PhotoService photoService = new PhotoService();
        inject(photoService, "photoDao", photoDao);
        inject(photoService, "s3Dao", s3Dao);
        inject(photoService, "userDao", userDao);
        inject(photoService, "folderDao", folderDao);
        UserService userService = new UserService();
        inject(userService, "userDao", userDao);
        inject(userService, "folderDao", folderDao);
        TagService tagService = new TagService();
        inject(tagService, "tagDao", tagDao);
        inject(tagService, "userDao", userDao);
        photoController = new PhotoController();
        inject(photoController, "photoService", photoService);
        inject(photoController, "userService", userService);
        inject(photoService, "tagService", tagService);
        inject(photoController, "utils", utils);
    }

    @Test
    public void TestUploadPhoto() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", "bar".getBytes());
        Tag tag = new Tag().setName("+ Add New Tag totoTestTag");
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        photoRequest = new PhotoRequest().setTitle("totoPhoto").setFile(file).setDescription("Photo de robert").setTags(gson.toJson(tags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57")).setFolderId(-1);
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null",Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", robert)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        when(utils.getUser()).thenReturn(robert);
        PhotoRequest photoRequest = new PhotoRequest().setTitle("totoPhoto").setFile(file).setTags(gson.toJson(tags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57"));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.upload(photoRequest);
        //THEN
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void TestGetUserPhotosUrl() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        when(s3Client.utilities().getUrl((Consumer<GetUrlRequest.Builder>) any()).toExternalForm()).thenReturn("testUrl");
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getUserPhotos(5, 1);
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
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        when(s3Client.utilities().getUrl((Consumer<GetUrlRequest.Builder>) any()).toExternalForm()).thenReturn("testUrl");
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getUserPhotos(5, 1);
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
    public void testGetInfosWithIllegalArgument() {
        //GIVEN
        createPhotoController();
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getUserPhotos(5, -1);
        //THEN
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("Argument illégal.", messageResponse.getMessage());
    }

    @Test
    public void testDownload() {
        // GIVEN
        createPhotoController();
        long id = 1L;
        long idUser = 2;
        String title = "test";
        String extension = "jpg";
        User user = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setLastname("test").setFirstname("test").setId(idUser);
        Photo photo = (Photo) new Photo().setFormat(extension).setTitle(title).setUser(user).setId(id);
        PhotoRequest photoRequest = new PhotoRequest().setId(id);
        ResponseBytes<GetObjectResponse> s3Object = mock(ResponseBytes.class);
        when(utils.getUser()).thenReturn(user);
        when(em.find(any(), anyLong())).thenReturn(photo);
        when(s3Client.getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()))).thenReturn(s3Object);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.download(photoRequest);
        //THEN
        verify(em, times(1)).find(Photo.class, id);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        PhotoResponse photoResponse = (PhotoResponse) responseEntity.getBody();
        assert photoResponse != null;
        Assert.assertEquals(title, photoResponse.getTitle());
        Assert.assertEquals(extension, photoResponse.getFormat());
    }

    @Test
    public void testDownloadUserNotAllowed() {
        //GIVEN
        createPhotoController();
        long id = 1L;
        int idUser = 2;
        User user = (User) new User().setId(idUser);
        Photo photo = (Photo) new Photo().setFormat("png").setTitle("test").setUser(user).setId(id);
        PhotoRequest photoRequest = new PhotoRequest().setId(id);
        when(utils.getUser()).thenReturn((User) new User().setId(1L));
        when(em.find(Photo.class, id)).thenReturn(photo);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.download(photoRequest);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testFailInvalidFormat() {
        //GIVEN
        createPhotoController();
        MockMultipartFile failFile = new MockMultipartFile("file", "orig", null, "bar".getBytes());
        Set<Tag> tags = new HashSet<>();
        Gson gson = new GsonBuilder().create();
        PhotoRequest photoRequest = new PhotoRequest().setTitle("totoPhoto").setFile(failFile).setTags(gson.toJson(tags)).setFolderId(-1);
        User user = new User().setLogin("toto").setPassword("password");
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(user);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null",Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", user)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        when(utils.getUser()).thenReturn(user);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.upload(photoRequest);
        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testDelete() {
        //GIVEN
        createPhotoController();
        long idPhoto = 1L;
        long[] ids = {1L};
        User user = (User) new User().setLogin("test@test").setId(2);
        Photo photo = (Photo) new Photo().setFormat("png").setTitle("test").setUser(user).setId(idPhoto);
        PhotosRequest photosRequest = new PhotosRequest().setIds(ids);
        when(utils.getUser()).thenReturn(user);
        when(em.find(Photo.class, ids[0])).thenReturn(photo);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.delete(photosRequest);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        verify(em, times(1)).remove(any());
    }

    @Test
    public void testDeleteUserNotAllowed() {
        //GIVEN
        createPhotoController();
        long idPhoto = 1L;
        long[] ids = {1L};
        User user = (User) new User().setLogin("test@test").setId(2);
        User userDiff = (User) new User().setLogin("test@test").setId(100);
        Photo photo = (Photo) new Photo().setFormat("png").setTitle("test").setUser(user).setId(idPhoto);
        PhotosRequest photosRequest = new PhotosRequest().setIds(ids);
        when(utils.getUser()).thenReturn(userDiff);
        when(em.find(Photo.class, ids[0])).thenReturn(photo);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.delete(photosRequest);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        verify(em, times(0)).remove(any());
    }

    @Test
    public void testDeleteServerDown() {
        //GIVEN
        createPhotoController();
        long idPhoto = 1L;
        long[] ids = {1L};
        User user = (User) new User().setLogin("test@test").setId(2);
        Photo photo = (Photo) new Photo().setFormat("png").setTitle("test").setUser(user).setId(idPhoto);
        PhotosRequest photosRequest = new PhotosRequest().setIds(ids);
        when(utils.getUser()).thenReturn(user);
        when(em.find(Photo.class, ids[0])).thenReturn(photo);
        doThrow(S3Exception.class).when(s3Client).deleteObject(any(DeleteObjectRequest.class));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.delete(photosRequest);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testUploadWithNotExistingFolder() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", "bar".getBytes());
        Tag tag = new Tag().setName("+ Add New Tag totoTestTag");
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        photoRequest = new PhotoRequest().setTitle("totoPhoto").setFile(file).setTags(gson.toJson(tags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57")).setFolderId(1);
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", user.getLogin())).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(user);
        when(em.find(Folder.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(jws, photoRequest);
        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("Le dossier n'existe pas.", messageResponse.getMessage());
    }

    @Test
    public void testUploadWithNotExistingUser() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", "bar".getBytes());
        Tag tag = new Tag().setName("+ Add New Tag totoTestTag");
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        photoRequest = new PhotoRequest().setTitle("totoPhoto").setFile(file).setTags(gson.toJson(tags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57")).setFolderId(1);
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", "toto").setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "toto")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenThrow(new NoResultException());
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(jws, photoRequest);
        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("L'utilisateur ou le dossier n'existe pas.", messageResponse.getMessage());
    }

    @Test
    public void testUploadWithUnauthorizedFolder() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", "bar".getBytes());
        Tag tag = new Tag().setName("+ Add New Tag totoTestTag");
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        photoRequest = new PhotoRequest().setTitle("totoPhoto").setFile(file).setTags(gson.toJson(tags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57")).setFolderId(2);
        User user = new User().setLogin("toto").setPassword("password");
        User other = (User) new User().setLogin("other").setPassword("Passw0rd").setId(2L);
        Folder otherFolder = (Folder) new Folder().setParentFolderId(1L).setName("otherFolder").setUser(other).setId(2L);
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", user.getLogin())).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(user);
        when(em.find(Folder.class, 2L)).thenReturn(otherFolder);
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.upload(jws, photoRequest);
        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("L'utilisateur n'a pas accès à ce dossier.", messageResponse.getMessage());
    }

    @Test
    public void testDownloadWithNotExistingPhoto() {
        //GIVEN
        createPhotoController();
        PhotoRequest photoRequest = new PhotoRequest().setId(1L);
        String token = Jwts.builder().claim("id", 1).setExpiration(
                new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.find(Photo.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.download(token, photoRequest);
        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("Le fichier n'existe pas", messageResponse.getMessage());
    }

    @Test
    public void testFolderPhoto() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        robert.addFolder(robertRoot);
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo().setTitle("Photo 1").setUser(robert).setFolder(robertRoot));
        listPhoto.add(new Photo().setTitle("Photo 2").setUser(robert).setFolder(robertRoot));
        String jws = Jwts.builder().claim("login", robert.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Robert")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(robert);
        when(em.createQuery("SELECT p FROM Photo p WHERE p.folder =: folder", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("folder", robertRoot)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.getPhotoByFolder(jws, 1L);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        PhotoListResponse response = (PhotoListResponse) responseEntity.getBody();
        assert response != null;
        Assert.assertEquals(2, response.getPhotoList().size());
        Assert.assertEquals("Photo 1", response.getPhotoList().get(0).getTitle());
    }

    @Test
    public void testFolderPhotoWithNotExistingFolder() {
        //GIVEN
        createPhotoController();
        String jws = Jwts.builder().claim("login", 1L).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.find(Folder.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.getPhotoByFolder(jws, 1L);
        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("Le dossier n'existe pas.", messageResponse.getMessage());
    }

    @Test
    public void testFolderPhotoWithNoAccessToFolder() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        User other = (User) new User().setLogin("other").setPassword("P@ssw0rd").setId(2);
        Folder otherFolder = (Folder) new Folder().setName("Other Folder").setParentFolderId(null).setUser(other).setId(1);
        other.addFolder(otherFolder);
        String jws = Jwts.builder().claim("login", robert.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.find(Folder.class, 1L)).thenReturn(otherFolder);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Robert")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(robert);
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.getPhotoByFolder(jws, 1L);
        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("L'utilisateur n'a pas accès à ce dossier.", messageResponse.getMessage());
    }

    @Test
    public void testFolderPhotoWithNotExistingUser() {
        //GIVEN
        createPhotoController();
        String jws = Jwts.builder().claim("login", "Robert").setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.find(Folder.class, 1L)).thenReturn(new Folder());
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Robert")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenThrow(new NoResultException());
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = photoController.getPhotoByFolder(jws, 1L);
        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("L'utilisateur n'existe pas.", messageResponse.getMessage());
    }
}
