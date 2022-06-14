package com.viseo.apph;

import com.viseo.apph.controller.FolderController;
import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Photo;
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
import org.springframework.security.core.parameters.P;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static com.viseo.apph.utils.Utils.inject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FolderTest {
    @Mock
    EntityManager em;

    @Mock
    TypedQuery<Folder> typedQueryFolder;

    @Mock
    TypedQuery<Photo> typedQueryPhoto;

    @Mock
    Utils utils;


    FolderService folderService;
    FolderController folderController;

    private void createFolderController() {
        FolderDao folderDao = new FolderDao();
        PhotoDao photoDao = new PhotoDao();
        inject(folderDao, "em", em);
        inject(photoDao, "em", em);
        UserDao userDao = new UserDao();
        inject(userDao, "em", em);
        folderService = new FolderService();
        inject(folderService, "folderDao", folderDao);
        inject(folderService, "userDao", userDao);
        inject(folderService, "photoDao", photoDao);
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
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
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
        Assert.assertEquals("Le nom du dossier ne peut pas dépasser 255 caractères.", messageResponse.getMessage());
    }

    @Test
    public void testMoveFolderWithFolderToBeMovedNull() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setMoveTo(1L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        when(utils.getUser()).thenReturn(robert);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.moveFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("Impossible de déplacer le dossier.", messageResponse.getMessage());
    }

    @Test
    public void testMoveFolderWithFoldersNull() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setMoveTo(1L).setFolderToBeMoved(0L);
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
        Assert.assertEquals("Dossier introuvable.", messageResponse.getMessage());
    }

    @Test
    public void testMoveRootFolder() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setMoveTo(1L).setFolderToBeMoved(0L);
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
        Assert.assertEquals("Dossier racine ne peut pas être déplacer.", messageResponse.getMessage());
    }

    @Test
    public void testMoveParentFolderToChild() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder parent = new Folder().setUser(robert).setParentFolderId(5L);
        Folder child = new Folder().setUser(robert).setParentFolderId(parent.getId());
        FolderRequest request = new FolderRequest().setMoveTo(child.getId()).setFolderToBeMoved(parent.getId());
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 0L)).thenReturn(child);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", robert)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn((Folder) new Folder().setParentFolderId(4L).setId(9L));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.moveFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("Dossier parent ne peut pas être déplacé vers son dossier enfant.", messageResponse.getMessage());
    }

    @Test
    public void testMoveFolderSameName() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder toBeMoved = (Folder) new Folder().setUser(robert).setName("Same Name").setParentFolderId(5L).setId(0L);
        Folder moveTo = (Folder) new Folder().setParentFolderId(42L).setId(2L);
        FolderRequest request = new FolderRequest().setMoveTo(moveTo.getId()).setFolderToBeMoved(toBeMoved.getId());
        List<Folder> folders = new ArrayList<>();
        folders.add((Folder) new Folder().setParentFolderId(2L).setName("Same Name").setId(9L));
        List<Folder> foldersToBeMoved = new ArrayList<>();
        foldersToBeMoved.add((Folder) new Folder().setParentFolderId(0L).setId(9L));
        List<Photo> photos = new ArrayList<>();
        photos.add(new Photo());
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 0L)).thenReturn(toBeMoved);
        when(em.find(Folder.class, 2L)).thenReturn(moveTo);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", robert)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn((Folder) new Folder().setId(99L));
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.parentFolderId = :folderId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("folderId", moveTo.getId())).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(folders);
        when(em.createQuery("SELECT p FROM Photo p WHERE p.folder =: folder", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("folder", toBeMoved)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(photos);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.parentFolderId = :folderId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("folderId", toBeMoved.getId())).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(folders);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.moveFolder(request);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("success: Le déplacement du dossier est terminé.", messageResponse.getMessage());
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
        FolderRequest request = new FolderRequest().setMoveTo(moveTo.getId()).setFolderToBeMoved(toBeMoved.getId());
        Folder parentFolder = (Folder) new Folder().setParentFolderId(null).setId(6L);
        when(utils.getUser()).thenReturn(robert);
        when(em.find(Folder.class, 0L)).thenReturn(toBeMoved);
        when(em.find(Folder.class, 2L)).thenReturn(moveTo);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.parentFolderId = :folderId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("folderId", moveTo.getId())).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(folders);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", robert)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.moveFolder(request);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("success: Le déplacement du dossier est terminé.", messageResponse.getMessage());
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
}