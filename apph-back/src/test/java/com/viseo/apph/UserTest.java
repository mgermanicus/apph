package com.viseo.apph;

import com.viseo.apph.controller.UserController;
import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.SettingDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Setting;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.SettingResponse;
import com.viseo.apph.dto.UserRequest;
import com.viseo.apph.security.JwtUtils;
import com.viseo.apph.security.UserDetailsImpl;
import com.viseo.apph.service.SettingService;
import com.viseo.apph.service.UserService;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import static com.viseo.apph.utils.Utils.inject;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery<User> typedQueryUser;
    @Mock
    TypedQuery<Long> typedQueryLong;
    @Mock
    TypedQuery<Folder> typedQueryFolder;
    @Mock
    TypedQuery<Setting> typedQuerySetting;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    com.viseo.apph.security.Utils utils;

    UserService userService;
    SettingService settingService;
    UserController userController;
    JwtUtils jwtUtils;

    private void createUserController() {
        UserDao userDao = new UserDao();
        FolderDao folderDao = new FolderDao();
        SettingDao settingDao = new SettingDao();
        inject(userDao, "em", em);
        inject(folderDao, "em", em);
        inject(settingDao, "em", em);
        userService = new UserService();
        inject(userService, "userDao", userDao);
        inject(userService, "folderDao", folderDao);
        settingService = new SettingService();
        inject(settingService, "settingDao", settingDao);
        jwtUtils = new JwtUtils();
        inject(userService, "jwtUtils", jwtUtils);
        userController = new UserController();
        inject(userController, "userService", userService);
        inject(userController, "settingService", settingService);
        inject(userService, "encoder", passwordEncoder);
        inject(userController, "utils", utils);
    }

    @Test
    public void testGetUserInfo() {
        //GIVEN
        createUserController();
        User user = new User().setLogin("toto").setPassword("password");
        when(utils.getUser()).thenReturn(user);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(new User().setLogin("toto").setPassword("password").setFirstname("firstname").setLastname("lastname"));
        when(typedQueryUser.setParameter("login", "toto")).thenReturn(typedQueryUser);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = userController.getUserInfo();
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testEditUserInfo() {
        //GIVEN
        User user = new User().setLogin("toto").setPassword("password").setFirstname("John").setLastname("Doe");
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(user);
        UserRequest request = new UserRequest()
                .setFirstName("Jean")
                .setLastName("Dupont")
                .setPassword("newPassword");
        createUserController();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(utils.getUser()).thenReturn(user);
        String jws = "Bearer "+jwtUtils.generateJwtToken(authentication);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(user);
        when(typedQueryUser.setParameter("login", "toto")).thenReturn(typedQueryUser);
        when(em.find(User.class, user.getId())).thenReturn(user);
        when(passwordEncoder.encode("newPassword")).thenReturn("newPassword");
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null",Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", user)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        //WHEN
        ResponseEntity<String> response = userController.editUserInfo(jws, request);
        //THEN
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(request.getFirstName(), user.getFirstname());
        Assert.assertEquals(request.getPassword(), user.getPassword());
        Assert.assertEquals(request.getLastName(), user.getLastname());
        Assert.assertEquals(request.getFirstName() + " " + request.getLastName(), parentFolder.getName());
    }

    @Test
    public void testEditUserLogin() {
        //GIVEN
        User user = new User().setLogin("login");
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(user);
        UserRequest request = new UserRequest().setLogin("newLogin");
        createUserController();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(utils.getUser()).thenReturn(user);
        String jws = "Bearer "+jwtUtils.generateJwtToken(authentication);        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(user);
        when(typedQueryUser.setParameter("login", user.getLogin())).thenReturn(typedQueryUser);
        when(em.find(User.class, user.getId())).thenReturn(user);
        when(em.createQuery("SELECT count(user) FROM User user WHERE user.login = :login", Long.class)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("login", request.getLogin())).thenReturn(typedQueryLong);
        when(typedQueryLong.getSingleResult()).thenReturn(0L);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null",Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", user)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        //WHEN
        ResponseEntity<String> response = userController.editUserInfo(jws, request);
        //THEN
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(request.getLogin(), user.getLogin());
    }

    @Test
    public void testEditUserInvalidLogin() {
        //GIVEN
        User user = new User().setLogin("login");
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(user);
        UserRequest request = new UserRequest().setLogin("alreadyTakenLogin");
        createUserController();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(utils.getUser()).thenReturn(user);
        String jws = "Bearer "+jwtUtils.generateJwtToken(authentication);        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(user);
        when(typedQueryUser.setParameter("login", user.getLogin())).thenReturn(typedQueryUser);
        when(em.find(User.class, user.getId())).thenReturn(user);
        when(em.createQuery("SELECT count(user) FROM User user WHERE user.login = :login", Long.class)).thenReturn(typedQueryLong);
        when(typedQueryLong.setParameter("login", request.getLogin())).thenReturn(typedQueryLong);
        when(typedQueryLong.getSingleResult()).thenReturn(1L);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null",Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", user)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        //WHEN
        ResponseEntity<String> response = userController.editUserInfo(jws, request);
        //THEN
        Assert.assertTrue(response.getStatusCode().isError());
        Assert.assertEquals("signup.error.emailUsed", response.getBody());
        Assert.assertEquals("login", user.getLogin());
    }

    @Test
    public void testEditUserNotFound() {
        //GIVEN
        User user = new User().setLogin("nonExistingLogin");
        UserRequest request = new UserRequest().setFirstName("John");
        createUserController();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(utils.getUser()).thenReturn(user);
        String jws = "Bearer "+jwtUtils.generateJwtToken(authentication);        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenThrow(new NoResultException());
        when(typedQueryUser.setParameter("login", user.getLogin())).thenReturn(typedQueryUser);
        //WHEN
        ResponseEntity<String> response = userController.editUserInfo(jws, request);
        //THEN
        Assert.assertTrue(response.getStatusCode().isError());
        Assert.assertEquals("user.error.sessionBindUserNotExist", response.getBody());
    }

    @Test
    public void testEditUserWithIllegalFirstNameLength() {
        //GIVEN
        User user = new User().setLogin("toto").setPassword("password").setFirstname("John").setLastname("Doe");
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(user);
        String name = RandomString.make(128);
        UserRequest request = new UserRequest()
                .setFirstName(name);
        createUserController();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(utils.getUser()).thenReturn(user);
        String jws = "Bearer "+jwtUtils.generateJwtToken(authentication);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(user);
        when(typedQueryUser.setParameter("login", "toto")).thenReturn(typedQueryUser);
        when(em.find(User.class, user.getId())).thenReturn(user);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null",Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", user)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        //WHEN
        ResponseEntity<String> response = userController.editUserInfo(jws, request);
        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertEquals("signup.error.nameOverChar", response.getBody());
    }

    @Test
    public void testEditUserWithIllegalLastNameLength() {
        //GIVEN
        User user = new User().setLogin("toto").setPassword("password").setFirstname("John").setLastname("Doe");
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(user);
        String name = RandomString.make(128);
        UserRequest request = new UserRequest()
                .setLastName(name);
        createUserController();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(utils.getUser()).thenReturn(user);
        String jws = "Bearer "+jwtUtils.generateJwtToken(authentication);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(user);
        when(typedQueryUser.setParameter("login", "toto")).thenReturn(typedQueryUser);
        when(em.find(User.class, user.getId())).thenReturn(user);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null",Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", user)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        //WHEN
        ResponseEntity<String> response = userController.editUserInfo(jws, request);
        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertEquals("signup.error.nameOverChar", response.getBody());
    }

    @Test
    public void testEditUserWithIllegalLoginLength() {
        //GIVEN
        User user = new User().setLogin("toto").setPassword("password").setFirstname("John").setLastname("Doe");
        Folder parentFolder = new Folder().setParentFolderId(null).setName("totoRoot").setUser(user);
        String login = RandomString.make(256);
        UserRequest request = new UserRequest()
                .setLogin(login);
        createUserController();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(utils.getUser()).thenReturn(user);
        String jws = "Bearer "+jwtUtils.generateJwtToken(authentication);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(user);
        when(typedQueryUser.setParameter("login", "toto")).thenReturn(typedQueryUser);
        when(em.find(User.class, user.getId())).thenReturn(user);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user = :user AND folder.parentFolderId is null",Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("user", user)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getSingleResult()).thenReturn(parentFolder);
        //WHEN
        ResponseEntity<String> response = userController.editUserInfo(jws, request);
        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assert.assertEquals("signup.error.loginOverChar", response.getBody());
    }

    @Test
    public void testGetSetting() {
        //GIVEN
        createUserController();
        when(em.createQuery("SELECT setting from Setting setting", Setting.class)).thenReturn(typedQuerySetting);
        when(typedQuerySetting.getSingleResult()).thenReturn(new Setting().setDownloadSize(10).setUploadSize(20));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = userController.getSettings();
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        SettingResponse response = (SettingResponse) responseEntity.getBody();
        assert response != null;
        Assert.assertEquals(10, response.getDownloadSize());
        Assert.assertEquals(20, response.getUploadSize());
    }
}
