package com.viseo.apph;

import com.viseo.apph.controller.FolderController;
import com.viseo.apph.dao.*;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.Setting;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.FolderRequest;
import com.viseo.apph.dto.FolderResponse;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.FolderService;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import static com.viseo.apph.utils.Utils.inject;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FolderTest {
    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Folder> typedQueryFolder, typedQuerySubFolder;

    @Mock
    TypedQuery<Photo> typedQueryPhoto, typedQuerySubFolderPhoto;

    @Mock
    TypedQuery<Setting> typedQuerySetting;

    @Mock
    TypedQuery<Long> typedQueryLong, typedQuerySubLong;

    @Mock
    TypedQuery<Tuple> typedQueryTuple;

    @Mock
    Stream<Tuple> streamTuple;

    @Mock
    Utils utils;

    @Mock
    S3Client s3Client;

    FolderService folderService;
    FolderController folderController;

    private void createFolderController() {
        FolderDao folderDao = new FolderDao();
        PhotoDao photoDao = new PhotoDao();
        UserDao userDao = new UserDao();
        SettingDao settingDao = new SettingDao();
        inject(folderDao, "em", em);
        inject(photoDao, "em", em);
        inject(userDao, "em", em);
        inject(settingDao, "em", em);
        S3Dao s3Dao = new S3Dao();
        s3Client = mock(S3Client.class, RETURNS_DEEP_STUBS);
        inject(s3Dao, "s3Client", s3Client);
        folderService = new FolderService();
        inject(folderService, "photoDao", photoDao);
        inject(folderService, "folderDao", folderDao);
        inject(folderService, "userDao", userDao);
        inject(folderService, "s3Dao", s3Dao);
        inject(folderService, "settingDao", settingDao);
        folderController = new FolderController();
        inject(folderController, "folderService", folderService);
        inject(folderController, "utils", utils);
    }

    @Test
    public void testCreateFolder() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setName("Robert Child").setParentFolderId(1L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setId(1);
        robert.addFolder(robertRoot);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user.id = :userId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("userId", 1L)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(robert.getFolders());

        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.createFolder(request);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        FolderResponse folderResponse = (FolderResponse) responseEntity.getBody();
        assert folderResponse != null;
        Assert.assertEquals("Robert Root", folderResponse.getName());
    }

    @Test
    public void testCreateFolderParentNotExist() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setName("Robert Child").setParentFolderId(1L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(3L).setUser(robert).setId(2);
        robert.addFolder(robertRoot);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user.id = :userId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("userId", 1L)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(robert.getFolders());

        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.createFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.notFound", messageResponse.getMessage());
    }

    @Test
    public void testCreateFolderWithExistingFolder() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setName("Robert Child").setParentFolderId(1L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        Folder robertChild = (Folder) new Folder().setName("Robert Child").setParentFolderId(1L).setUser(robert).setId(2);
        robert.addFolder(robertRoot).addFolder(robertChild);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        doThrow(new DataIntegrityViolationException("SQLException")).when(em).persist(any());
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.createFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.existingFolder", messageResponse.getMessage());
    }

    @Test
    public void testCreateFolderWithoutParentFolder() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setName("Robert Child").setParentFolderId(null);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        when(utils.getUser()).thenReturn(robert);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.createFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.root", messageResponse.getMessage());
    }

    @Test
    public void testCreateFolderWithNonExistingParentFolder() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setName("Robert Child").setParentFolderId(1L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        Folder robertChild = (Folder) new Folder().setName("Robert Child").setParentFolderId(1L).setUser(robert).setId(2);
        robert.addFolder(robertRoot).addFolder(robertChild);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.createFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.notFound", messageResponse.getMessage());
    }

    @Test
    public void testCreateFolderDifferentUser() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setName("Chris Child").setParentFolderId(1L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        User chris = (User) new User().setLogin("Chris").setPassword("P@ssw0rd").setId(2).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        robert.addFolder(robertRoot);
        when(utils.getUser()).thenReturn(chris);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.createFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.unauthorized", messageResponse.getMessage());
    }

    @Test
    public void testCreateFolderWithInvalidNameLength() {
        //GIVEN
        createFolderController();
        String name = RandomString.make(256);
        FolderRequest request = new FolderRequest().setName(name).setParentFolderId(1L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        when(utils.getUser()).thenReturn(robert);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.createFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.folderNameOverChar", messageResponse.getMessage());
    }

    @Test
    public void testMoveFolderWithFolderToBeMovedNull() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setDestinationFolderId(1L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        when(utils.getUser()).thenReturn(robert);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.moveFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.moveFolder", messageResponse.getMessage());
    }

    @Test
    public void testMoveFolderWithFoldersNull() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setDestinationFolderId(1L).setFolderIdToBeMoved(0L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(null);
        when(em.find(Folder.class, 0L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.moveFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.nullFolder", messageResponse.getMessage());
    }

    @Test
    public void testMoveRootFolder() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setDestinationFolderId(1L).setFolderIdToBeMoved(0L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder parent = new Folder().setUser(robert);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(em.find(Folder.class, 1L)).thenReturn(new Folder().setUser(robert));
        when(em.find(Folder.class, 0L)).thenReturn(new Folder().setUser(robert));
        when(typedQueryFolder.setParameter("user", robert)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parent);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.moveFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.moveFolder", messageResponse.getMessage());
    }

    @Test
    public void testMoveParentFolderToChild() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder parent = new Folder().setUser(robert).setParentFolderId(5L);
        Folder child = new Folder().setUser(robert).setParentFolderId(parent.getId());
        FolderRequest request = new FolderRequest().setDestinationFolderId(child.getId()).setFolderIdToBeMoved(parent.getId());
        Map<Long, Long> map = new HashMap<>();
        map.put(9L, 4L);
        map.put(4L, -1L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 0L)).thenReturn(child);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", robert)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn((Folder) new Folder().setParentFolderId(4L).setId(9L));
        when(em.createQuery("SELECT folder.id as childId, folder.parentFolderId as parentId from Folder folder WHERE folder.user = :user", Tuple.class)).thenReturn(typedQueryTuple);
        when(typedQueryTuple.setParameter("user", robert)).thenReturn(typedQueryTuple);
        when(typedQueryTuple.getResultStream()).thenReturn(streamTuple);
        when(streamTuple.collect(any())).thenReturn(map);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.moveFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.moveFolder", messageResponse.getMessage());
    }

    @Test
    public void testMoveFolderSameName() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder toBeMoved = (Folder) new Folder().setUser(robert).setName("Same Name").setParentFolderId(5L).setId(0L);
        Folder moveTo = (Folder) new Folder().setParentFolderId(42L).setId(2L);
        Folder moveToChild = (Folder) new Folder().setParentFolderId(2L).setName("Same Name").setId(9L);
        FolderRequest request = new FolderRequest().setDestinationFolderId(moveTo.getId()).setFolderIdToBeMoved(toBeMoved.getId());
        List<Folder> folders = new ArrayList<>();
        folders.add(moveToChild);
        Photo photo = new Photo().setTitle("Photo").setFormat(".png").setFolder(toBeMoved);
        Map<Long, Long> map = new HashMap<>();
        map.put(2L, 42L);
        map.put(42L, -1L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 0L)).thenReturn(toBeMoved);
        when(em.find(Folder.class, 2L)).thenReturn(moveTo);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", robert)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn((Folder) new Folder().setId(99L));
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.parentFolderId = :parentId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("parentId", moveTo.getId())).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("parentId", toBeMoved.getId())).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(folders);
        when(typedQueryFolder.setParameter("parentId", 9L)).thenReturn(typedQuerySubFolder);
        when(typedQuerySubFolder.getResultList()).thenReturn(new ArrayList<>());
        when(em.createQuery("SELECT count(photo) FROM Photo photo WHERE photo.folder = :folder AND photo.title = :title AND photo.format = :format", Long.class)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("folder", moveToChild)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("title", "Photo")).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("format", ".png")).thenReturn(typedQueryLong);
        when(typedQueryLong.getSingleResult()).thenReturn(1L);
        when(typedQueryLong.setParameter("title", "Photo_1")).thenReturn(typedQuerySubLong);
        when(typedQuerySubLong.setParameter("format", ".png")).thenReturn(typedQuerySubLong);
        when(typedQuerySubLong.getSingleResult()).thenReturn(0L);
        when(em.createQuery("SELECT folder.id as childId, folder.parentFolderId as parentId from Folder folder WHERE folder.user = :user", Tuple.class)).thenReturn(typedQueryTuple);
        when(typedQueryTuple.setParameter("user", robert)).thenReturn(typedQueryTuple);
        when(typedQueryTuple.getResultStream()).thenReturn(streamTuple);
        when(streamTuple.collect(any())).thenReturn(map);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.moveFolder(request);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("success: folder.successMove", messageResponse.getMessage());
    }

    @Test
    public void testMoveFolder() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder toBeMoved = (Folder) new Folder().setUser(robert).setParentFolderId(5L).setId(0L);
        Folder moveTo = (Folder) new Folder().setParentFolderId(42L).setId(2L);
        List<Folder> folders = new ArrayList<>();
        folders.add((Folder) new Folder().setParentFolderId(4L).setName("Name").setId(9L));
        FolderRequest request = new FolderRequest().setDestinationFolderId(moveTo.getId()).setFolderIdToBeMoved(toBeMoved.getId());
        Folder parentFolder = (Folder) new Folder().setParentFolderId(null).setId(6L);
        Map<Long, Long> map = new HashMap<>();
        map.put(2L, 42L);
        map.put(42L, 6L);
        map.put(6L, -1L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 0L)).thenReturn(toBeMoved);
        when(em.find(Folder.class, 2L)).thenReturn(moveTo);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.parentFolderId = :parentId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("parentId", moveTo.getId())).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(folders);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", robert)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        when(em.createQuery("SELECT folder.id as childId, folder.parentFolderId as parentId from Folder folder WHERE folder.user = :user", Tuple.class)).thenReturn(typedQueryTuple);
        when(typedQueryTuple.setParameter("user", robert)).thenReturn(typedQueryTuple);
        when(typedQueryTuple.getResultStream()).thenReturn(streamTuple);
        when(streamTuple.collect(any())).thenReturn(map);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.moveFolder(request);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("success: folder.successMove", messageResponse.getMessage());
    }

    @Test
    public void testDeleteFolderDeleteContent() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        Folder folder = (Folder) new Folder().setUser(robert).setParentFolderId(2L).setId(1);
        FolderRequest request = new FolderRequest().setId(1L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(folder);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.parentFolderId = :parentId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("parentId", 1L)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(new ArrayList<>());
        //WHEN
        ResponseEntity<IResponseDto> response = folderController.deleteFolder(request);
        //THEN
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.successDelete", messageResponse.getMessage());
    }

    @Test
    public void testDeleteFolderMoveContent() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        Folder rootFolder = (Folder) new Folder().setUser(robert).setParentFolderId(null).setId(0);
        Folder folder = (Folder) new Folder().setUser(robert).setParentFolderId(0L).setId(1);
        Folder folderChild = (Folder) new Folder().setUser(robert).setParentFolderId(1L).setId(2);
        List<Folder> folders = new ArrayList<>();
        folders.add(folderChild);
        FolderRequest request = new FolderRequest().setId(1L).setDestinationFolderId(0L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 0L)).thenReturn(rootFolder);
        when(em.find(Folder.class, 1L)).thenReturn(folder);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.parentFolderId = :parentId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("parentId", 1L)).thenReturn(typedQueryFolder).thenReturn(typedQuerySubFolder);
        when(typedQueryFolder.getResultList()).thenReturn(folders);
        when(typedQueryFolder.setParameter("parentId", 0L)).thenReturn(typedQuerySubFolder);
        when(typedQuerySubFolder.getResultList()).thenReturn(new ArrayList<>());
        //WHEN
        ResponseEntity<IResponseDto> response = folderController.deleteFolder(request);
        //THEN
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.successDelete", messageResponse.getMessage());
    }

    @Test
    public void testDeleteFolderNoSrcFolder() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        FolderRequest request = new FolderRequest().setId(1L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDto> response = folderController.deleteFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.notExist", messageResponse.getMessage());
    }

    @Test
    public void testDeleteFolderUnauthorizedFolder() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        User other = (User) new User().setLogin("Other").setId(2);
        Folder folder = (Folder) new Folder().setUser(other).setParentFolderId(2L).setId(1);
        FolderRequest request = new FolderRequest().setId(1L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(folder);
        //WHEN
        ResponseEntity<IResponseDto> response = folderController.deleteFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.unauthorized", messageResponse.getMessage());
    }

    @Test
    public void testDeleteFolderDeleteRootFolder() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        Folder folder = (Folder) new Folder().setUser(robert).setParentFolderId(null).setId(1);
        FolderRequest request = new FolderRequest().setId(1L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(folder);
        //WHEN
        ResponseEntity<IResponseDto> response = folderController.deleteFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.deleteFolder", messageResponse.getMessage());
    }

    @Test
    public void testDeleteFolderMoveWithOutDstFolder() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        Folder folder = (Folder) new Folder().setUser(robert).setParentFolderId(0L).setId(1);
        FolderRequest request = new FolderRequest().setId(1L).setDestinationFolderId(0L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 0L)).thenReturn(null);
        when(em.find(Folder.class, 1L)).thenReturn(folder);
        //WHEN
        ResponseEntity<IResponseDto> response = folderController.deleteFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.notExist", messageResponse.getMessage());
    }

    @Test
    public void testDeleteFolderNotMovableFolder() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setId(1);
        Folder srcfolder = (Folder) new Folder().setUser(robert).setParentFolderId(0L).setId(1);
        Folder dstolder = (Folder) new Folder().setUser(robert).setParentFolderId(1L).setId(2);
        FolderRequest request = new FolderRequest().setId(1L).setDestinationFolderId(2L);
        Map<Long, Long> map = new HashMap<>();
        map.put(1L, 2L);
        map.put(2L, 1L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(srcfolder);
        when(em.find(Folder.class, 2L)).thenReturn(dstolder);
        when(em.createQuery("SELECT folder.id as childId, folder.parentFolderId as parentId from Folder folder WHERE folder.user = :user", Tuple.class)).thenReturn(typedQueryTuple);
        when(typedQueryTuple.setParameter("user", robert)).thenReturn(typedQueryTuple);
        when(typedQueryTuple.getResultStream()).thenReturn(streamTuple);
        when(streamTuple.collect(any())).thenReturn(map);
        //WHEN
        ResponseEntity<IResponseDto> response = folderController.deleteFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assert messageResponse != null;
        Assert.assertEquals("folder.error.moveFolder", messageResponse.getMessage());
    }

    @Test
    public void testGetFoldersByUser() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder folder = (Folder) new Folder().setParentFolderId(5L).setId(6L);
        List<Folder> folders = new ArrayList<>();
        folders.add(folder);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.parentFolderId = :parentId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("parentId", 5L)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(folders);
        when(em.find(Folder.class, 5L)).thenReturn((Folder) new Folder().setName("folderParent").setParentFolderId(0L).setVersion(0).setId(5L));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.getFoldersByUser(5);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetFoldersByUserWithRootFolder() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder parentFolder = (Folder) new Folder().setParentFolderId(null).setId(6L);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", robert)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.getFoldersByUser(-1);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testGetFoldersByUserWithArgumentIllegal() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder parentFolder = (Folder) new Folder().setParentFolderId(null).setId(6L);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.parentFolderId = :parentId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("parentId", 100000000L)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.getFoldersByUser(100000000);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("request.error.illegalArgument", messageResponse.getMessage());
    }

    @Test
    public void testDownloadFolder() {
        //GIVEN
        createFolderController();
        String name = RandomString.make(256);
        FolderRequest request = new FolderRequest().setId(1L).setName(name);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        Folder robertChild = (Folder) new Folder().setName("Robert Child").setParentFolderId(1L).setUser(robert).setId(2);
        Photo photo_1 = (Photo) new Photo().setFormat("png").setTitle("Test_1").setUser(robert).setId(1L);
        Photo photo_2 = (Photo) new Photo().setFormat("png").setTitle("Test_1").setUser(robert).setId(2L);
        GetObjectResponse response = mock(GetObjectResponse.class);
        ResponseBytes<GetObjectResponse> s3Object = ResponseBytes.fromByteArray(response, "".getBytes(StandardCharsets.UTF_8));
        when(s3Client.getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()))).thenReturn(s3Object);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        when(em.createQuery("SELECT p FROM Photo p WHERE p.folder =: folder", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("folder", robertRoot)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("folder", robertChild)).thenReturn(typedQuerySubFolderPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(Arrays.asList(photo_1, photo_2));
        when(typedQuerySubFolderPhoto.getResultList()).thenReturn(null);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.parentFolderId = :parentId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("parentId", 1L)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("parentId", 2L)).thenReturn(typedQuerySubFolder);
        when(typedQueryFolder.getResultList()).thenReturn(Collections.singletonList(robertChild));
        when(typedQuerySubFolder.getResultList()).thenReturn(null);
        when(em.createQuery("SELECT setting from Setting setting", Setting.class)).thenReturn(typedQuerySetting);
        when(typedQuerySetting.getSingleResult()).thenReturn(new Setting().setDownloadSize(10).setUploadSize(20));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.downloadFolderToZip(request);
        //THEN
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        FolderResponse folderResponse = (FolderResponse) responseEntity.getBody();
        assert folderResponse != null;
        Assert.assertNotNull(folderResponse.getData());
    }

    @Test
    public void testDownloadFolderUnauthorized() {
        //GIVEN
        createFolderController();
        String name = RandomString.make(256);
        FolderRequest request = new FolderRequest().setId(1L).setName(name);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        User chris = (User) new User().setLogin("Chris").setPassword("P@ssw0rd").setId(2).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        when(utils.getUser()).thenReturn(chris);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.downloadFolderToZip(request);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    public void testDownloadFolderNotFound() {
        //GIVEN
        createFolderController();
        String name = RandomString.make(256);
        FolderRequest request = new FolderRequest().setId(1L).setName(name);
        User chris = (User) new User().setLogin("Chris").setPassword("P@ssw0rd").setId(2).setVersion(0);
        when(utils.getUser()).thenReturn(chris);
        when(em.find(Folder.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.downloadFolderToZip(request);
        //THEN
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void testDownloadZipLarge() {
        //GIVEN
        createFolderController();
        String name = RandomString.make(256);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Photo photo_1 = (Photo) new Photo().setFormat("png").setTitle("test").setUser(robert).setId(1L);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        FolderRequest request = new FolderRequest().setId(1L).setName(name);
        GetObjectResponse response = mock(GetObjectResponse.class);
        ResponseBytes<GetObjectResponse> s3Object = ResponseBytes.fromByteArray(response, "".getBytes(StandardCharsets.UTF_8));
        when(utils.getUser()).thenReturn(robert);
        when(em.find(eq(Folder.class), anyLong())).thenReturn(robertRoot);
        when(s3Client.getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()))).thenReturn(s3Object);
        when(em.createQuery("SELECT p FROM Photo p WHERE p.folder =: folder", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("folder", robertRoot)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(Arrays.asList(photo_1, photo_1));
        when(em.createQuery("SELECT setting from Setting setting", Setting.class)).thenReturn(typedQuerySetting);
        when(typedQuerySetting.getSingleResult()).thenReturn(new Setting().setDownloadSize(0).setUploadSize(0));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.downloadFolderToZip(request);
        //THEN
        Assert.assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, responseEntity.getStatusCode());
    }
}