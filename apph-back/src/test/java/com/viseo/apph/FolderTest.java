package com.viseo.apph;

import com.viseo.apph.controller.FolderController;
import com.viseo.apph.dao.FolderDAO;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.FolderResponse;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.service.FolderService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FolderTest {
    @Mock
    EntityManager em;

    @Mock
    TypedQuery typedQuery;

    FolderService folderService;
    FolderController folderController;

    private void createFolderController() {
        FolderDAO folderDAO = new FolderDAO();
        inject(folderDAO, "em", em);
        UserDAO userDAO = new UserDAO();
        inject(userDAO, "em", em);
        folderService = new FolderService();
        inject(folderService, "folderDAO", folderDAO);
        inject(folderService, "userDAO", userDAO);
        folderController = new FolderController();
        inject(folderController, "folderService", folderService);
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
    public void testGetFoldersByUser() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        Folder robertChild1 = (Folder) new Folder().setName("Robert Child 1").setParentFolderId(1L).setUser(robert).setId(2);
        Folder robertChild2 = (Folder) new Folder().setName("Robert Child 2").setParentFolderId(1L).setUser(robert).setId(3);
        robert.addFolder(robertRoot).addFolder(robertChild1).addFolder(robertChild2);
        when(em.find(User.class, 1L)).thenReturn(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user.id = :userId", Folder.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", 1L)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(robert.getFolders());
        //WHEN
        ResponseEntity responseEntity = folderController.getFoldersByUser(1L);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        FolderResponse folderResponse = (FolderResponse) responseEntity.getBody();
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
        when(em.find(User.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity responseEntity = folderController.getFoldersByUser(1L);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is4xxClientError());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        Assert.assertEquals("User not found.", messageResponse.getMessage());
    }

    @Test
    public void testGetFoldersByUserNoParentFolder() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder folder = (Folder) new Folder().setName("Folder").setParentFolderId(1L).setId(1);
        robert.addFolder(folder);
        when(em.find(User.class, 1L)).thenReturn(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user.id = :userId", Folder.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", 1L)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(robert.getFolders());
        //WHEN
        ResponseEntity responseEntity = folderController.getFoldersByUser(1L);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is4xxClientError());
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        Assert.assertEquals("Parent folder not found.", messageResponse.getMessage());
    }
}
