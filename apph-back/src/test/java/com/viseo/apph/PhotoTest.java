package com.viseo.apph;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viseo.apph.controller.PhotoController;
import com.viseo.apph.dao.*;
import com.viseo.apph.domain.*;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.dto.*;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.TagService;
import com.viseo.apph.service.UserService;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

import static com.viseo.apph.utils.Utils.inject;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PhotoTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery<Photo> typedQueryPhoto;
    @Mock
    TypedQuery<Folder> typedQueryFolder;
    @Mock
    TypedQuery<Long> typedQueryLong;
    @Mock
    TypedQuery<Long> typedQueryInvalidLong;
    @Mock
    TypedQuery<Setting> typedQuerySetting;
    @Mock
    Utils utils;
    @Mock
    S3Client s3Client;

    long zipMaxSize = 50;

    PhotoController photoController;

    private void createPhotoController() {
        PhotoDao photoDao = new PhotoDao();
        UserDao userDao = new UserDao();
        TagDao tagDao = new TagDao();
        SettingDao settingDao = new SettingDao();
        FolderDao folderDao = new FolderDao();
        inject(photoDao, "em", em);
        inject(userDao, "em", em);
        inject(tagDao, "em", em);
        inject(folderDao, "em", em);
        inject(settingDao, "em", em);
        S3Dao s3Dao = new S3Dao();
        s3Client = mock(S3Client.class, RETURNS_DEEP_STUBS);
        inject(s3Dao, "s3Client", s3Client);
        PhotoService photoService = new PhotoService();
        inject(photoService, "photoDao", photoDao);
        inject(photoService, "s3Dao", s3Dao);
        inject(photoService, "userDao", userDao);
        inject(photoService, "folderDao", folderDao);
        inject(photoService, "settingDao", settingDao);
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
    public void testUploadPhoto() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", "bar".getBytes());
        Tag tag = new Tag().setName("+ Add New Tag totoTestTag");
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        PhotoRequest photoRequest = new PhotoRequest().setTitle("totoPhoto").setFile(file).setDescription("Photo de robert").setTags(gson.toJson(tags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57")).setFolderId(-1);
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", robert)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        when(em.createQuery("SELECT count(photo) FROM Photo photo WHERE photo.folder = :folder AND photo.title = :title AND photo.format = :format", Long.class)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("folder", null)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("title", "totoPhoto")).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("format", ".png")).thenReturn(typedQueryLong);
        when(typedQueryLong.getSingleResult()).thenReturn(0L);
        when(utils.getUser()).thenReturn(robert);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.upload(photoRequest);
        //THEN
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testEditPhotoInfos() {
        //GIVEN
        createPhotoController();
        Set<Tag> oldTags = new HashSet<>();
        Tag oneOldTag = new Tag().setName("+ Add New Tag tag");
        oldTags.add(oneOldTag);
        User user = new User().setLogin("toto").setPassword("password");
        Photo oldPhoto = new Photo().setCreationDate(LocalDate.now()).setShootingDate(LocalDate.now()).setFormat(".png").setTitle("title").setDescription("desc").setSize(1).setUser(user).setTags(oldTags);
        Set<Tag> newTags = new HashSet<>();
        Tag oneNewTag = new Tag().setName("+ Add New Tag new tag");
        newTags.add(oneNewTag);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        PhotoRequest photoRequest = new PhotoRequest().setTitle("newTitle").setTags(gson.toJson(newTags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57")).setDescription("newDesc").setId(1L);
        when(em.find(Photo.class, 1L)).thenReturn(oldPhoto);
        when(utils.getUser()).thenReturn(user);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.editInfos(photoRequest);
        //THEN
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("newTitle", oldPhoto.getTitle());
        assertEquals("newDesc", oldPhoto.getDescription());
        assertNotNull(oldPhoto.getShootingDate());
        assertEquals(1, oldPhoto.getTags().size());
        assertTrue(oldPhoto.getTags().contains(oneNewTag));
    }

    @Test
    public void testEditNotFoundPhoto() {
        //GIVEN
        createPhotoController();
        PhotoRequest photoRequest = new PhotoRequest().setShootingDate("13/05/2022, 12:07:57");
        when(em.find(Photo.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.editInfos(photoRequest);
        //THEN
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("photo.error.notFound", messageResponse.getMessage());
    }

    @Test
    public void testEditWithInvalidDate() {
        //GIVEN
        createPhotoController();
        PhotoRequest photoRequest = new PhotoRequest().setShootingDate("\"Invalid Date\"");
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.editInfos(photoRequest);
        //THEN
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("photo.error.invalidDate", messageResponse.getMessage());
    }

    @Test
    public void testGetUserPhotosUrl() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());
        FilterRequest filterRequest = new FilterRequest().setPage(1).setPageSize(5);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user ORDER BY p.id DESC", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        when(s3Client.utilities().getUrl((Consumer<GetUrlRequest.Builder>) any()).toExternalForm()).thenReturn("testUrl");
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getUserPhotos(filterRequest);
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
        LocalDate shootingDate = LocalDate.now();
        LocalDate modificationDate = LocalDate.now();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Tag tag = new Tag().setUser(robert).setName("robertTag");
        listPhoto.add((Photo) new Photo().setSize(10).setTitle("photo 1").setCreationDate(LocalDate.now()).setModificationDate(modificationDate).setShootingDate(shootingDate).setDescription("description").addTag(tag).setId(1L));
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        FilterRequest filterRequest = new FilterRequest().setPage(1).setPageSize(5);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user ORDER BY p.id DESC", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        when(s3Client.utilities().getUrl((Consumer<GetUrlRequest.Builder>) any()).toExternalForm()).thenReturn("testUrl");
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getUserPhotos(filterRequest);
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
        Assert.assertEquals(modificationDate, photo.getModificationDate());
        Assert.assertEquals(shootingDate, photo.getShootingDate());
        Assert.assertEquals("description", photo.getDescription());
        Assert.assertTrue(photo.getTags().contains(tag));
        Assert.assertEquals(1, photo.getId());
        Assert.assertEquals("testUrl", photo.getUrl());
    }

    @Test
    public void testGetInfosWithFilter() {
        //GIVEN
        createPhotoController();
        List<Photo> listPhoto = new ArrayList<>();
        Date creationDate = new Date();
        LocalDate shootingDate = LocalDate.now();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Tag tag = new Tag().setUser(robert).setName("robertTag");
        listPhoto.add((Photo) new Photo().setSize(10).setTitle("photo 1").setCreationDate(LocalDate.now()).setShootingDate(shootingDate).setDescription("description").addTag(tag).setId(1L));
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        FilterDto[] filterDtos = new FilterDto[]{
                new FilterDto().setField("title").setOperator("contain").setValue("p"),
                new FilterDto().setField("creationDate").setOperator("strictlyInferior").setValue("1"),
                new FilterDto().setField("shootingDate").setOperator("strictlySuperior").setValue("2"),
                new FilterDto().setField("shootingDate").setOperator("superiorEqual").setValue("3"),
                new FilterDto().setField("creationDate").setOperator("inferiorEqual").setValue("4"),
                new FilterDto().setField("creationDate").setOperator("equal").setValue("5"),
                new FilterDto().setField("description").setOperator("is").setValue("cool"),
                new FilterDto().setField("title").setOperator("is").setValue("photo")
        };
        FilterRequest filterRequest = new FilterRequest().setPage(1).setPageSize(5).setFilterList(filterDtos);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT p FROM Photo p JOIN p.tags t WHERE p.user = :user AND (p.title LIKE '%' || ?1 || '%'  OR p.title LIKE ?2 ) AND (p.description LIKE ?3 ) AND (p.creationDate < ?4  OR p.creationDate <= ?5  OR p.creationDate = ?6 ) AND (p.shootingDate > ?7  OR p.shootingDate >= ?8 ) GROUP BY p.id ORDER BY p.id DESC", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        when(s3Client.utilities().getUrl((Consumer<GetUrlRequest.Builder>) any()).toExternalForm()).thenReturn("testUrl");
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getUserPhotos(filterRequest);
        //THEN
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
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
    public void testGetInfosWithFilterTags() {
        //GIVEN
        createPhotoController();
        List<Photo> listPhoto = new ArrayList<>();
        Date creationDate = new Date();
        LocalDate shootingDate = LocalDate.now();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Tag tag = new Tag().setUser(robert).setName("robertTag");
        listPhoto.add((Photo) new Photo().setSize(10).setTitle("photo 1").setCreationDate(LocalDate.now()).setShootingDate(shootingDate).setDescription("description").addTag(tag).setId(1L));
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        listPhoto.add(new Photo());
        FilterDto[] filterDtos = new FilterDto[]{
                new FilterDto().setField("tags").setValue("p1"),
                new FilterDto().setField("tags").setValue("p2"),
                new FilterDto().setField("tags").setValue("p3"),
                new FilterDto().setField("title").setOperator("contain").setValue("p"),
                new FilterDto().setField("title").setOperator("is").setValue("photo")
        };
        FilterRequest filterRequest = new FilterRequest().setPage(1).setPageSize(5).setFilterList(filterDtos);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT p FROM Photo p JOIN p.tags t WHERE p.user = :user AND (p.title LIKE '%' || ?1 || '%'  OR p.title LIKE ?2 ) AND (?3 IN (select t.name from p.tags t)  OR ?4 IN (select t.name from p.tags t)  OR ?5 IN (select t.name from p.tags t) ) GROUP BY p.id ORDER BY p.id DESC", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        when(s3Client.utilities().getUrl((Consumer<GetUrlRequest.Builder>) any()).toExternalForm()).thenReturn("testUrl");
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getUserPhotos(filterRequest);
        //THEN
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
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
        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user ORDER BY p.id DESC", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        FilterRequest filterRequest = new FilterRequest().setPage(-1).setPageSize(5);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getUserPhotos(filterRequest);
        //THEN
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("photoTable.error.illegalArgument", messageResponse.getMessage());
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
    public void testDownloadS3Down() {
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
        doThrow(S3Exception.class).when(s3Client).getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.download(photoRequest);
        //THEN
        verify(em, times(1)).find(Photo.class, id);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
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
        PhotoRequest photoRequest = new PhotoRequest().setTitle("totoPhoto").setDescription("Description").setFile(failFile).setTags(gson.toJson(tags)).setFolderId(-1);
        User user = new User().setLogin("toto").setPassword("password");
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(user);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", user)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        when(em.createQuery("SELECT count(photo) FROM Photo photo WHERE photo.folder = :folder AND photo.title = :title", Long.class)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("folder", parentFolder)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("title", "totoPhoto")).thenReturn(typedQueryLong);
        when(typedQueryLong.getSingleResult()).thenReturn(0L);
        when(utils.getUser()).thenReturn(user);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.upload(photoRequest);
        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testUploadS3Down() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", "bar".getBytes());
        Tag tag = new Tag().setName("+ Add New Tag totoTestTag");
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        PhotoRequest photoRequest = new PhotoRequest().setTitle("totoPhoto").setFile(file).setDescription("Photo de robert").setTags(gson.toJson(tags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57")).setFolderId(-1);
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", robert)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        when(em.createQuery("SELECT count(photo) FROM Photo photo WHERE photo.folder = :folder AND photo.title = :title AND photo.format = :format", Long.class)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("folder", null)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("title", "totoPhoto")).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("format", ".png")).thenReturn(typedQueryLong);
        when(typedQueryLong.getSingleResult()).thenReturn(0L);
        when(utils.getUser()).thenReturn(robert);
        doThrow(S3Exception.class).when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.upload(photoRequest);
        //THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
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
        PhotoRequest photoRequest = new PhotoRequest().setTitle("totoPhoto").setDescription("Description").setFile(file).setTags(gson.toJson(tags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57")).setFolderId(1);
        User user = new User().setLogin("toto").setPassword("password");
        when(utils.getUser()).thenReturn(user);
        when(em.find(Folder.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.upload(photoRequest);
        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("upload.error.folderNotExist", messageResponse.getMessage());
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
        PhotoRequest photoRequest = new PhotoRequest().setTitle("totoPhoto").setDescription("Description").setFile(file).setTags(gson.toJson(tags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57")).setFolderId(2);
        User user = new User().setLogin("toto").setPassword("password");
        User other = (User) new User().setLogin("other").setPassword("Passw0rd").setId(2L);
        Folder otherFolder = (Folder) new Folder().setParentFolderId(1L).setName("otherFolder").setUser(other).setId(2L);
        when(utils.getUser()).thenReturn(user);
        when(em.find(Folder.class, 2L)).thenReturn(otherFolder);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.upload(photoRequest);
        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.accessDenied", messageResponse.getMessage());
    }

    @Test
    public void testUploadWithExistingPhotoTitle() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", "bar".getBytes());
        Tag tag = new Tag().setName("+ Add New Tag totoTestTag");
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        PhotoRequest photoRequest = new PhotoRequest().setTitle("totoPhoto").setFile(file).setDescription("Photo de toto").setTags(gson.toJson(tags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57")).setFolderId(-1);
        User user = new User().setLogin("toto").setPassword("password");
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(user);
        when(utils.getUser()).thenReturn(user);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", user)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        when(em.createQuery("SELECT count(photo) FROM Photo photo WHERE photo.folder = :folder AND photo.title = :title AND photo.format = :format", Long.class)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("folder", null)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("title", "totoPhoto")).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("format", ".png")).thenReturn(typedQueryLong);
        when(typedQueryLong.getSingleResult()).thenReturn(1L);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.upload(photoRequest);
        //THEN
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.titleAlreadyUsed", messageResponse.getMessage());
    }

    @Test
    public void testUploadWithInvalidTitleLength() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", "bar".getBytes());
        Tag tag = new Tag().setName("+ Add New Tag totoTestTag");
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String title = RandomString.make(256);
        PhotoRequest photoRequest = new PhotoRequest().setTitle(title).setDescription("Description").setFile(file).setTags(gson.toJson(tags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57")).setFolderId(1);
        User user = new User().setLogin("toto").setPassword("password");
        when(utils.getUser()).thenReturn(user);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.upload(photoRequest);
        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("photo.error.titleOrDescriptionOverChar", messageResponse.getMessage());
    }

    @Test
    public void testUploadWithInvalidTagLength() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", "bar".getBytes());
        String tagName = RandomString.make(256);
        Tag tag = new Tag().setName("+ Add New Tag " + tagName);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        PhotoRequest photoRequest = new PhotoRequest().setTitle("totoPhoto").setDescription("Description").setFile(file).setTags(gson.toJson(tags)).setShootingDate(gson.toJson("13/05/2022, 12:07:57")).setFolderId(-1);
        User user = new User().setLogin("toto").setPassword("password");
        when(utils.getUser()).thenReturn(user);
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(user);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", user)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        when(em.createQuery("SELECT count(photo) FROM Photo photo WHERE photo.folder = :folder AND photo.title = :title AND photo.format = :format", Long.class)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("folder", null)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("title", "totoPhoto")).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("format", ".png")).thenReturn(typedQueryLong);
        when(typedQueryLong.getSingleResult()).thenReturn(0L);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.upload(photoRequest);
        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("photo.error.tagOverChar", messageResponse.getMessage());
    }

    @Test
    public void testDownloadWithNotExistingPhoto() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        PhotoRequest photoRequest = new PhotoRequest().setId(1L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Photo.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.download(photoRequest);
        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("download.error.fileNotExist", messageResponse.getMessage());
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
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        when(em.createQuery("SELECT p FROM Photo p WHERE p.folder =: folder", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("folder", robertRoot)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getPhotosByFolder(1L);
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
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getPhotosByFolder(1L);
        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.notExist", messageResponse.getMessage());
    }

    @Test
    public void testFolderPhotoWithNoAccessToFolder() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        User other = (User) new User().setLogin("other").setPassword("P@ssw0rd").setId(2);
        Folder otherFolder = (Folder) new Folder().setName("Other Folder").setParentFolderId(null).setUser(other).setId(1);
        other.addFolder(otherFolder);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(otherFolder);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getPhotosByFolder(1L);
        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.accessDenied", messageResponse.getMessage());
    }

    @Test
    public void testMovePhoto() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        User other = (User) new User().setLogin("Other").setId(2);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        Folder robertChild = (Folder) new Folder().setName("Robert Child").setParentFolderId(1L).setUser(robert).setId(2);
        Photo photoValid = (Photo) new Photo().setTitle("Photo Valid").setFormat(".png").setUser(robert).setFolder(robertRoot).setId(1L);
        Photo photoOfOther = (Photo) new Photo().setTitle("Photo").setFormat(".png").setUser(other).setId(1L);
        Photo photoAlreadyInFolder = (Photo) new Photo().setTitle("Photo Already in Folder").setFormat(".png").setUser(robert).setFolder(robertChild).setId(1L);
        Photo photoAlreadyExist = (Photo) new Photo().setTitle("Photo Already Exist").setFormat(".png").setUser(robert).setFolder(robertRoot).setId(1L);
        PhotosRequest request = new PhotosRequest().setIds(new long[]{1L, 2L, 3L, 4L, 5L}).setFolderId(2L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        when(em.find(Folder.class, 2L)).thenReturn(robertChild);
        when(em.find(Photo.class, 1L)).thenReturn(photoValid);
        when(em.find(Photo.class, 2L)).thenReturn(photoOfOther);
        when(em.find(Photo.class, 3L)).thenReturn(photoAlreadyInFolder);
        when(em.find(Photo.class, 4L)).thenReturn(photoAlreadyExist);
        when(em.find(Photo.class, 5L)).thenReturn(null);
        when(em.createQuery("SELECT count(photo) FROM Photo photo WHERE photo.folder = :folder AND photo.title = :title AND photo.format = :format", Long.class)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("folder", robertChild)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("title", "Photo Valid")).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("format", ".png")).thenReturn(typedQueryLong);
        when(typedQueryLong.getSingleResult()).thenReturn(0L);
        when(typedQueryLong.setParameter("title", "Photo Already Exist")).thenReturn(typedQueryInvalidLong);
        when(typedQueryInvalidLong.setParameter("format", ".png")).thenReturn(typedQueryInvalidLong);
        when(typedQueryInvalidLong.getSingleResult()).thenReturn(1L);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.movePhotosToFolder(request);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        MessageListResponse response = (MessageListResponse) responseEntity.getBody();
        assert response != null;
        Assert.assertEquals(5, response.getMessageList().size());
        Assert.assertEquals("error: folder.error.oneOf.notBelongUser", response.getMessageList().get(0));
        Assert.assertEquals("warning: folder.error.oneOf.alreadyExist", response.getMessageList().get(1));
        Assert.assertEquals("error: folder.error.oneOf.existingName", response.getMessageList().get(2));
        Assert.assertEquals("error: folder.error.oneOf.photoNotExist", response.getMessageList().get(3));
        Assert.assertEquals("success: photo.successMove", response.getMessageList().get(4));
    }

    @Test
    public void testMovePhotoWithNotExistingFolder() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        PhotosRequest request = new PhotosRequest().setFolderId(1L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.movePhotosToFolder(request);
        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.notExist", messageResponse.getMessage());
    }

    @Test
    public void testMovePhotoWithUnauthorizedFolder() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        User other = (User) new User().setLogin("Other").setId(2);
        Folder otherRoot = (Folder) new Folder().setName("Other Root").setParentFolderId(null).setUser(other).setId(1);
        PhotosRequest request = new PhotosRequest().setFolderId(1L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(otherRoot);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.movePhotosToFolder(request);
        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.accessDenied", messageResponse.getMessage());
    }

    @Test
    public void testDownloadZipLarge() {
        //GIVEN
        zipMaxSize = 0;
        createPhotoController();
        long[] ids = {1L, 1L};
        User user = (User) new User().setLogin("test@test").setId(2);
        Photo photo_1 = (Photo) new Photo().setFormat("png").setTitle("test").setUser(user).setId(ids[0]);
        PhotosRequest photosRequest = new PhotosRequest().setIds(ids);
        GetObjectResponse response = mock(GetObjectResponse.class);
        ResponseBytes<GetObjectResponse> s3Object = ResponseBytes.fromByteArray(response, "".getBytes(StandardCharsets.UTF_8));
        when(utils.getUser()).thenReturn(user);
        when(em.find(any(), anyLong())).thenReturn(photo_1);
        when(s3Client.getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()))).thenReturn(s3Object);
        when(em.createQuery("SELECT setting from Setting setting", Setting.class)).thenReturn(typedQuerySetting);
        when(typedQuerySetting.getSingleResult()).thenReturn(new Setting().setDownloadSize(0).setUploadSize(0));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.downloadZip(photosRequest);
        //THEN
        Assert.assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, responseEntity.getStatusCode());
    }

    @Test
    public void testDownloadZipDuplicate() {
        //GIVEN
        createPhotoController();
        long[] ids = {1L, 1L};
        User user = (User) new User().setLogin("test@test").setId(2);
        Photo photo_1 = (Photo) new Photo().setFormat("png").setTitle("test").setUser(user).setId(ids[0]);
        PhotosRequest photosRequest = new PhotosRequest().setIds(ids);
        GetObjectResponse response = mock(GetObjectResponse.class);
        ResponseBytes<GetObjectResponse> s3Object = ResponseBytes.fromByteArray(response, "".getBytes(StandardCharsets.UTF_8));
        when(utils.getUser()).thenReturn(user);
        when(em.find(any(), anyLong())).thenReturn(photo_1);
        when(s3Client.getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()))).thenReturn(s3Object);
        when(em.createQuery("SELECT setting from Setting setting", Setting.class)).thenReturn(typedQuerySetting);
        when(typedQuerySetting.getSingleResult()).thenReturn(new Setting().setDownloadSize(10).setUploadSize(10));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.downloadZip(photosRequest);
        //THEN
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(em, times(2)).find(eq(Photo.class), anyLong());
        PhotoResponse photoResponse = (PhotoResponse) responseEntity.getBody();
        assert photoResponse != null;
        Assert.assertEquals("APPH-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()), photoResponse.getTitle());
        Assert.assertEquals(".zip", photoResponse.getFormat());
        Assert.assertNotNull(photoResponse.getData());
    }

    @Test
    public void testDownloadZipUserNotAllowed() {
        //GIVEN
        createPhotoController();
        long[] ids = {1L, 1L};
        User user = (User) new User().setLogin("test@test").setId(2);
        Photo photo_1 = (Photo) new Photo().setFormat("png").setTitle("test").setUser(user).setId(ids[0]);
        PhotosRequest photosRequest = new PhotosRequest().setIds(ids);
        GetObjectResponse response = mock(GetObjectResponse.class);
        ResponseBytes<GetObjectResponse> s3Object = ResponseBytes.fromByteArray(response, "".getBytes(StandardCharsets.UTF_8));
        when(utils.getUser()).thenReturn((User) new User().setId(1));
        when(em.find(any(), anyLong())).thenReturn(photo_1);
        when(s3Client.getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()))).thenReturn(s3Object);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.downloadZip(photosRequest);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    public void testDownloadZipNotFound() {
        //GIVEN
        createPhotoController();
        long[] ids = {1L, 1L};
        PhotosRequest photosRequest = new PhotosRequest().setIds(ids);
        when(utils.getUser()).thenReturn((User) new User().setId(1));
        when(em.find(any(), anyLong())).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.downloadZip(photosRequest);
        //THEN
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void testChangePhotoFile() {
        //GIVEN
        createPhotoController();
        String imageString = RandomString.make(2000);
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", imageString.getBytes());
        User robert = (User) new User().setLogin("Robert").setId(1);
        PhotoRequest photoRequest = new PhotoRequest().setFile(file).setId(0);
        Photo photo = (Photo) new Photo().setFormat(".jpeg").setSize(50).setUser(robert).setId(0);
        when(em.find(Photo.class, 0L)).thenReturn(photo);
        when(utils.getUser()).thenReturn(robert);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.changePhotoFile(photoRequest);
        //THEN
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(".png", photo.getFormat());
        assertEquals(2, photo.getSize(), 0.1);
    }

    @Test
    public void testChangePhotoFileS3Down() {
        //GIVEN
        createPhotoController();
        String imageString = RandomString.make(2000);
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", imageString.getBytes());
        User robert = (User) new User().setLogin("Robert").setId(1);
        PhotoRequest photoRequest = new PhotoRequest().setFile(file).setId(0);
        Photo photo = (Photo) new Photo().setFormat(".jpeg").setSize(50).setUser(robert).setId(0);
        when(em.find(Photo.class, 0L)).thenReturn(photo);
        when(utils.getUser()).thenReturn(robert);
        doThrow(S3Exception.class).when(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.changePhotoFile(photoRequest);
        //THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void testChangePhotoFilePhotoNotFound() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", "bar".getBytes());
        User robert = (User) new User().setLogin("Robert").setId(1);
        PhotoRequest photoRequest = new PhotoRequest().setFile(file).setId(0);
        when(em.find(Photo.class, 0L)).thenReturn(null);
        when(utils.getUser()).thenReturn(robert);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.changePhotoFile(photoRequest);
        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("download.error.fileNotExist", messageResponse.getMessage());
    }

    @Test
    public void testChangePhotoFileUnauthorizedAccess() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "image/png", "bar".getBytes());
        User robert = (User) new User().setLogin("Robert").setId(1);
        User other = (User) new User().setLogin("Other").setId(2);
        Photo photo = (Photo) new Photo().setFormat(".jpeg").setSize(50).setUser(other).setId(0);
        PhotoRequest photoRequest = new PhotoRequest().setFile(file).setId(0);
        when(em.find(Photo.class, 0L)).thenReturn(photo);
        when(utils.getUser()).thenReturn(robert);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.changePhotoFile(photoRequest);
        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("download.error.accessDenied", messageResponse.getMessage());
    }

    @Test
    public void testChangePhotoFileInvalidFormat() {
        //GIVEN
        createPhotoController();
        MockMultipartFile file = new MockMultipartFile("file", "orig", "fake", "bar".getBytes());
        User robert = (User) new User().setLogin("Robert").setId(1);
        PhotoRequest photoRequest = new PhotoRequest().setFile(file).setId(0);
        Photo photo = (Photo) new Photo().setFormat(".jpeg").setSize(50).setUser(robert).setId(0);
        when(em.find(Photo.class, 0L)).thenReturn(photo);
        when(utils.getUser()).thenReturn(robert);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.changePhotoFile(photoRequest);
        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("upload.error.wrongFormat", messageResponse.getMessage());
    }

    @Test
    public void testUpdatePhotoFolder() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        PhotoRequest request = new PhotoRequest().setDescription("description").setTitle("title").setFolderId(-1).setId(1);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Photo.class, 1L)).thenReturn(new Photo().setUser(robert));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.updatePhotoInfo(request);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testUpdatePhotoFolderWithFolderId() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        Folder folder = (Folder) new Folder().setId(1);
        PhotoRequest request = new PhotoRequest().setDescription("description").setTitle("title").setFolderId(1).setId(42);

        when(utils.getUser()).thenReturn(robert);
        when(em.find(Photo.class, 42L)).thenReturn((Photo) new Photo().setUser(robert).setId(42));
        when(em.find(Folder.class, 1L)).thenReturn(folder);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.updatePhotoInfo(request);
        //THEN
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("photo.successDelete", messageResponse.getMessage());
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testUpdatePhotoFolderWithNotExistingPhoto() {
        createPhotoController();
        PhotoRequest request = new PhotoRequest().setDescription("description").setTitle("title").setFolderId(-1).setId(1);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.updatePhotoInfo(request);
        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("photo.error.notExist", messageResponse.getMessage());
    }

    @Test
    public void testUpdatePhotoFolderWithUnauthorized() {
        createPhotoController();
        PhotoRequest request = new PhotoRequest().setDescription("description").setTitle("title").setFolderId(-1).setId(1);
        when(utils.getUser()).thenReturn(new User());
        when(em.find(Photo.class, 1L)).thenReturn(new Photo().setUser((User) new User().setId(42)));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.updatePhotoInfo(request);
        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("action.forbidden", messageResponse.getMessage());
    }

    private void testSorting(String field, String sort) {
        //GIVEN
        createPhotoController();
        List<Photo> listPhoto = new ArrayList<>();
        User robert = (User) new User().setLogin("Robert").setId(1);
        listPhoto.add(new Photo());
        SortDto sortModel = new SortDto().setField(field).setSort(sort);
        FilterRequest filterRequest = new FilterRequest().setPage(1).setPageSize(5).setSortModel(sortModel);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT p FROM Photo p WHERE p.user = :user ORDER BY p." + field + " " + (sort.equals("asc") ? "ASC" : "DESC"), Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(listPhoto);
        when(s3Client.utilities().getUrl((Consumer<GetUrlRequest.Builder>) any()).toExternalForm()).thenReturn("testUrl");
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getUserPhotos(filterRequest);
        //THEN
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testSortTitle() {
        testSorting("title", "asc");
    }

    @Test
    public void testSortDescription() {
        testSorting("description", "desc");
    }

    @Test
    public void testSortCreationDate() {
        testSorting("creationDate", "asc");
    }

    @Test
    public void testSortShootingDate() {
        testSorting("shootingDate", "desc");
    }

    @Test
    public void testSortSize() {
        testSorting("size", "asc");
    }

    @Test
    public void testSortDefault() {
        testSorting("id", "desc");
    }

    @Test
    public void testGetUrlsByIdsWithEmptyIds() {
        //GIVEN
        createPhotoController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        when(utils.getUser()).thenReturn(robert);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getPhotosByIds(new ArrayList<>());
        //THEN
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("photo.maySelected", messageResponse.getMessage());
    }

    @Test
    public void testGetUrlsByIds() {
        //GIVEN
        createPhotoController();
        List<Long> ids = new ArrayList<>();
        ids.add(0L);
        ids.add(1L);
        User robert = (User) new User().setLogin("Robert").setId(1);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT photo FROM Photo photo WHERE photo.id IN :ids AND photo.user = :user", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("ids", ids)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("user", robert)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(new ArrayList<>());
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = photoController.getPhotosByIds(ids);
        //THEN
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }
}
