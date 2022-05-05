package com.viseo.apph;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.controller.UserController;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.EditUserRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.security.Key;
import java.util.Date;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery typedQuery;
    @Mock
    TypedQuery existByLoginQuery;
    @Mock
    PasswordEncoder passwordEncoder;

    UserService userService;
    UserController userController;

    private void createUserController() {
        UserDAO userDAO = new UserDAO();
        inject(userDAO, "em", em);
        userService = new UserService();
        inject(userService, "userDAO", userDAO);
        userController = new UserController();
        inject(userController, "userService", userService);
        inject(userService, "encoder", passwordEncoder);
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
    public void testGetUserInfo() {
        //GIVEN
        createUserController();
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new User().setLogin("toto").setPassword("password").setFirstname("firstname").setLastname("lastname"));
        when(typedQuery.setParameter("login", "toto")).thenReturn(typedQuery);
        //WHEN
        ResponseEntity responseEntity = userController.getUserInfo(jws);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testFailUserNotFind() {
        //GIVEN
        createUserController();
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", "dumb_toto").setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        //WHEN
        ResponseEntity responseEntity = userController.getUserInfo(jws);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void testFailTokenExpired() {
        //GIVEN
        createUserController();
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis())).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new User().setLogin("toto").setPassword("password").setFirstname("firstname").setLastname("lastname"));
        //WHEN
        ResponseEntity responseEntity = userController.getUserInfo(jws);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testFailWrongSignature() {
        //GIVEN
        createUserController();
        User user = new User().setLogin("toto").setPassword("password");
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis())).signWith(key).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new User().setLogin("toto").setPassword("password").setFirstname("firstname").setLastname("lastname"));
        //WHEN
        ResponseEntity responseEntity = userController.getUserInfo(jws);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testEditUserInfo() {
        //GIVEN
        User user = new User().setLogin("toto").setPassword("password").setFirstname("John").setLastname("Doe");
        EditUserRequest request = new EditUserRequest()
                .setFirstname("Jean")
                .setLastname("Dupont")
                .setPassword("newPassword");
        createUserController();
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(user);
        when(typedQuery.setParameter("login", "toto")).thenReturn(typedQuery);
        when(em.find(User.class, user.getId())).thenReturn(user);
        when(passwordEncoder.encode("newPassword")).thenReturn("newPassword");
        //WHEN
        ResponseEntity response = userController.editUserInfo(jws, request);
        //THEN
        Assert.assertTrue(response.getStatusCode().is2xxSuccessful());
        Assert.assertEquals(request.getFirstname(), user.getFirstname());
        Assert.assertEquals(request.getPassword(), user.getPassword());
        Assert.assertEquals(request.getLastname(), user.getLastname());
    }

    @Test
    public void testEditUserLogin() {
        //GIVEN
        User user = new User().setLogin("login");
        EditUserRequest request = new EditUserRequest().setLogin("newLogin");
        createUserController();
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(user);
        when(typedQuery.setParameter("login", user.getLogin())).thenReturn(typedQuery);
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
        EditUserRequest request = new EditUserRequest().setLogin("alreadyTakenLogin");
        createUserController();
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(user);
        when(typedQuery.setParameter("login", user.getLogin())).thenReturn(typedQuery);
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
        EditUserRequest request = new EditUserRequest().setFirstname("John");
        createUserController();
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenThrow(new NoResultException());
        when(typedQuery.setParameter("login", user.getLogin())).thenReturn(typedQuery);
        //WHEN
        ResponseEntity response = userController.editUserInfo(jws, request);
        //THEN
        Assert.assertTrue(response.getStatusCode().isError());
        Assert.assertEquals("L'utilisateur lié à cette session n'existe pas", response.getBody());
    }

    @Test
    public void testEditUserTokenExpired() {
        //GIVEN
        EditUserRequest request = new EditUserRequest();
        createUserController();
        String jws = Jwts.builder().claim("login", "").setExpiration(new Date(System.currentTimeMillis())).signWith(JwtConfig.getKey()).compact();
        //WHEN
        ResponseEntity response = userController.editUserInfo(jws, request);
        //THEN
        Assert.assertTrue(response.getStatusCode().isError());
        Assert.assertEquals("La session a expiré. Veuillez vous reconnecter", response.getBody());
    }

}
