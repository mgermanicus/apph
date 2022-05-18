package com.viseo.apph;

import com.viseo.apph.controller.UserController;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.UserRequest;
import com.viseo.apph.security.JwtUtils;
import com.viseo.apph.security.UserDetailsImpl;
import com.viseo.apph.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import static com.viseo.apph.utils.Utils.inject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery<User> userTypedQuery;
    @Mock
    TypedQuery<Long> existByLoginQuery;
    @Mock
    PasswordEncoder passwordEncoder;
   @Mock
    com.viseo.apph.security.Utils utils;

    UserService userService;
    UserController userController;
    JwtUtils jwtUtils;

    private void createUserController() {
        UserDao userDao = new UserDao();
        inject(userDao, "em", em);
        userService = new UserService();
        inject(userService, "userDao", userDao);
        jwtUtils = new JwtUtils();
        inject(userService, "jwtUtils", jwtUtils);
        userController = new UserController();
        inject(userController, "userService", userService);
        inject(userService, "encoder", passwordEncoder);
        inject(userController, "utils", utils);

    }

    @Test
    public void testGetUserInfo() {
        //GIVEN
        createUserController();
        User user = new User().setLogin("toto").setPassword("password");
        when(utils.getUser()).thenReturn(user);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(userTypedQuery);
        when(userTypedQuery.getSingleResult()).thenReturn(new User().setLogin("toto").setPassword("password").setFirstname("firstname").setLastname("lastname"));
        when(userTypedQuery.setParameter("login", "toto")).thenReturn(userTypedQuery);
        //WHEN
        ResponseEntity responseEntity = userController.getUserInfo();
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testEditUserInfo() {
        //GIVEN
        User user = new User().setLogin("toto").setPassword("password").setFirstname("John").setLastname("Doe");
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
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(userTypedQuery);
        when(userTypedQuery.getSingleResult()).thenReturn(user);
        when(userTypedQuery.setParameter("login", "toto")).thenReturn(userTypedQuery);
        when(em.find(User.class, user.getId())).thenReturn(user);
        when(passwordEncoder.encode("newPassword")).thenReturn("newPassword");
        //WHEN
        ResponseEntity response = userController.editUserInfo(jws, request);
        //THEN
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(request.getFirstName(), user.getFirstname());
        Assert.assertEquals(request.getPassword(), user.getPassword());
        Assert.assertEquals(request.getLastName(), user.getLastname());
    }

    @Test
    public void testEditUserLogin() {
        //GIVEN
        User user = new User().setLogin("login");
        UserRequest request = new UserRequest().setLogin("newLogin");
        createUserController();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(utils.getUser()).thenReturn(user);
        String jws = "Bearer "+jwtUtils.generateJwtToken(authentication);        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(userTypedQuery);
        when(userTypedQuery.getSingleResult()).thenReturn(user);
        when(userTypedQuery.setParameter("login", user.getLogin())).thenReturn(userTypedQuery);
        when(em.find(User.class, user.getId())).thenReturn(user);
        when(em.createQuery("SELECT count(user) FROM User user WHERE user.login = :login", Long.class)).thenReturn(existByLoginQuery);
        when(existByLoginQuery.setParameter("login", request.getLogin())).thenReturn(existByLoginQuery);
        when(existByLoginQuery.getSingleResult()).thenReturn(0L);
        //WHEN
        ResponseEntity response = userController.editUserInfo(jws, request);
        //THEN
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(request.getLogin(), user.getLogin());
    }

    @Test
    public void testEditUserInvalidLogin() {
        //GIVEN
        User user = new User().setLogin("login");
        UserRequest request = new UserRequest().setLogin("alreadyTakenLogin");
        createUserController();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(utils.getUser()).thenReturn(user);
        String jws = "Bearer "+jwtUtils.generateJwtToken(authentication);        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(userTypedQuery);
        when(userTypedQuery.getSingleResult()).thenReturn(user);
        when(userTypedQuery.setParameter("login", user.getLogin())).thenReturn(userTypedQuery);
        when(em.find(User.class, user.getId())).thenReturn(user);
        when(em.createQuery("SELECT count(user) FROM User user WHERE user.login = :login", Long.class)).thenReturn(existByLoginQuery);
        when(existByLoginQuery.setParameter("login", request.getLogin())).thenReturn(existByLoginQuery);
        when(existByLoginQuery.getSingleResult()).thenReturn(1L);
        //WHEN
        ResponseEntity response = userController.editUserInfo(jws, request);
        //THEN
        Assert.assertTrue(response.getStatusCode().isError());
        Assert.assertEquals("Ce login est déjà pris", response.getBody());
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
        String jws = "Bearer "+jwtUtils.generateJwtToken(authentication);        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(userTypedQuery);
        when(userTypedQuery.getSingleResult()).thenThrow(new NoResultException());
        when(userTypedQuery.setParameter("login", user.getLogin())).thenReturn(userTypedQuery);
        //WHEN
        ResponseEntity response = userController.editUserInfo(jws, request);
        //THEN
        Assert.assertTrue(response.getStatusCode().isError());
        Assert.assertEquals("L'utilisateur lié à cette session n'existe pas", response.getBody());
    }
}
