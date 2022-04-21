package com.viseo.apph;

import com.viseo.apph.controller.AuthController;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.UserRequest;
import com.viseo.apph.service.UserService;
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
import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.Silent.class)
public class AuthTest {
    @Mock
    EntityManager em;
    UserService userService;
    AuthController authController;
    @Mock
    TypedQuery typedQuery;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    NoResultException noResultException;

    private void createAuthController() {
        UserDAO userDAO = new UserDAO();
        inject(userDAO, "em", em);
        userService = new UserService();
        inject(userService,"encoder",passwordEncoder);
        inject(userService,"userDAO",userDAO);
        authController = new AuthController();
        inject(authController,"userService",userService);
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
    public void testLogin() {
        //GIVEN
        createAuthController();
        UserRequest userRequest = new UserRequest().setLogin("tintin").setPassword("password");
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new User().setLogin("tintin").setPassword("password"));
        when(typedQuery.setParameter("login","tintin")).thenReturn(typedQuery);
        when(passwordEncoder.matches("password","password")).thenReturn(true);
        //WHEN
        ResponseEntity responseEntity = authController.login(userRequest);
        //THEN
        Assert.assertTrue( responseEntity.getStatusCode().is2xxSuccessful());
    }
    @Test
    public void testFailLoginUserNotFind() {
        //GIVEN
        createAuthController();
        UserRequest userRequest = new UserRequest().setLogin("tintin").setPassword("password");
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new User().setLogin("tintin").setPassword("password"));
        when(typedQuery.setParameter("login","tintin")).thenThrow(noResultException);
        when(passwordEncoder.matches("password","password")).thenReturn(true);
        //WHEN
        ResponseEntity responseEntity = authController.login(userRequest);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().equals(HttpStatus.UNAUTHORIZED));
    }
    @Test
    public void testFailLoginPasswordNotMatch() {
        //GIVEN
        createAuthController();
        UserRequest userRequest = new UserRequest().setLogin("tintin").setPassword("password");
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new User().setLogin("tintin").setPassword("password"));
        when(typedQuery.setParameter("login","tintin")).thenReturn(typedQuery);
        when(passwordEncoder.matches("password","fakePassword")).thenReturn(false);
        //WHEN
        ResponseEntity responseEntity = authController.login(userRequest);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().equals(HttpStatus.UNAUTHORIZED));
    }
}
