package com.viseo.apph.controller;

import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.User;
import com.viseo.apph.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import static com.viseo.apph.utils.Utils.inject;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery typedQuery;
    @Mock
    com.viseo.apph.security.Utils utils;
    UserService userService;
    UserController userController;

    private void createUserController() {
        UserDao userDao = new UserDao();
        inject(userDao, "em", em);
        userService = new UserService();
        inject(userService, "userDao", userDao);
        userController = new UserController();
        inject(userController, "userService", userService);
    }

    @Test
    public void testGetUserInfo() {
        //GIVEN
        createUserController();
        User user = new User().setLogin("toto").setPassword("password");
        UserController.utils = utils;
        when(utils.getUser()).thenReturn(user);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new User().setLogin("toto").setPassword("password").setFirstname("firstname").setLastname("lastname"));
        when(typedQuery.setParameter("login", "toto")).thenReturn(typedQuery);
        //WHEN
        ResponseEntity responseEntity = userController.getUserInfo();
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testFail() {
        //GIVEN
        createUserController();
        User user = new User().setLogin("toto").setPassword("password");
        UserController.utils = utils;
        when(utils.getUser()).thenThrow(new IllegalArgumentException());
        //WHEN
        ResponseEntity responseEntity = userController.getUserInfo();
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.FORBIDDEN);
    }

}
