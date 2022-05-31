package com.viseo.apph;

import com.viseo.apph.controller.FolderController;
import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Folder;
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
    TypedQuery<User> typedQueryUser;

    @Mock
    TypedQuery<Folder> typedQueryFolder;

    @Mock Utils utils;


    FolderService folderService;
    FolderController folderController;

    private void createFolderController() {
        FolderDao folderDao = new FolderDao();
        inject(folderDao, "em", em);
        UserDao userDao = new UserDao();
        inject(userDao, "em", em);
        folderService = new FolderService();
        inject(folderService, "folderDao", folderDao);
        inject(folderService, "userDao", userDao);
        folderController = new FolderController();
        inject(folderController, "folderService", folderService);
        inject(folderController, "utils", utils);

    }

    @Test
    public void testGetFoldersByUser() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        Folder robertChild1 = (Folder) new Folder().setName("Robert Child 1").setParentFolderId(1L).setUser(robert).setId(2);
        Folder robertChild2 = (Folder) new Folder().setName("Robert Child 2").setParentFolderId(1L).setUser(robert).setId(3);
        robert.addFolder(robertRoot).addFolder(robertChild1).addFolder(robertChild2);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Robert")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user.id = :userId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("userId", 1L)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(robert.getFolders());
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.getFoldersByUser();
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        FolderResponse folderResponse = (FolderResponse) responseEntity.getBody();
        assert folderResponse != null;
        Assert.assertEquals("Robert Root", folderResponse.getName());
        Assert.assertEquals(1, folderResponse.getId());
        Assert.assertEquals(0, folderResponse.getVersion());
        Assert.assertEquals(2, folderResponse.getChildrenFolders().size());
        Assert.assertNull(folderResponse.getParentFolderId());
    }

    @Test
    public void testGetFoldersByUserNoUser() {
        //GIVEN
        createFolderController();
        when(utils.getUser()).thenReturn((User)new User().setLogin("Not a User").setId(1));
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Not a User")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenThrow(new NoResultException());
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.getFoldersByUser();
        //THEN
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("L'utilisateur n'existe pas.", messageResponse.getMessage());
    }

    @Test
    public void testGetFoldersByUserNoParentFolder() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder folder = (Folder) new Folder().setName("Folder").setParentFolderId(1L).setId(1);
        List<Folder> folders = new ArrayList<>();
        folders.add(folder);
        when(utils.getUser()).thenReturn(robert);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Robert")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user.id = :userId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("userId", 1L)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(folders);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.getFoldersByUser();
        //THEN
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("Dossier parent introuvable.", messageResponse.getMessage());
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
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Robert")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(robert);
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
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Robert")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        doThrow(new DataIntegrityViolationException("SQLException")).when(em).persist(any());
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.createFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("Le dossier existe déjà dans le dossier actuel.", messageResponse.getMessage());
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
        Assert.assertEquals("Impossible de créer un dossier racine.", messageResponse.getMessage());
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
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Robert")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.createFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("Dossier parent introuvable.", messageResponse.getMessage());
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
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.setParameter("login", "Chris")).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(chris);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = folderController.createFolder(request);
        //THEN
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        assert messageResponse != null;
        Assert.assertEquals("L'utilisateur n'a pas accès à ce dossier.", messageResponse.getMessage());
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
}