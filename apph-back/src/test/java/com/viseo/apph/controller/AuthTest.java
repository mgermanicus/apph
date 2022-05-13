package com.viseo.apph.controller;

import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.ERole;
import com.viseo.apph.domain.Role;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.LoginRequest;
import com.viseo.apph.dto.UserRequest;
import com.viseo.apph.security.JwtUtils;
import com.viseo.apph.security.UserDetailsImpl;
import com.viseo.apph.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static com.viseo.apph.utils.Utils.inject;
import static org.junit.Assert.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
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
    AuthenticationManager authenticationManager;

    private void createAuthController() {
        UserDao userDao = new UserDao();
        FolderDao folderDao = new FolderDao();
        inject(userDao, "em", em);
        inject(folderDao, "em", em);
        userService = new UserService();
        inject(userService, "encoder", passwordEncoder);
        inject(userService, "userDao", userDao);
        inject(userService, "folderDao", folderDao);
        authController = new AuthController();
        inject(authController, "userService", userService);
    }

    @Test
    public void testLogin() {
        //GIVEN
        createAuthController();
        Set<Role> set = new HashSet<Role>();
        set.add(new Role(ERole.ROLE_USER));
        User user = (User) new User().setLogin("tintin")
                .setPassword("P@ssw0rd")
                .setLastname("test")
                .setFirstname("test")
                .setRoles(set)
                .setId(1);
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        authController.authenticationManager = authenticationManager;
        authController.jwtUtils = new JwtUtils();
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.mockStatic(SecurityContextHolder.class).when(SecurityContextHolder::getContext).thenReturn(securityContext);
        LoginRequest loginRequest = new LoginRequest().setUsername("tintin").setPassword("P@ssw0rd");

        //WHEN
        ResponseEntity responseEntity = authController.login(loginRequest);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testRegister() {
        //GIVEN
        createAuthController();
        UserRequest userRequest = new UserRequest().setLogin("tintin").setPassword("password");
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenThrow(new NoResultException());
        when(passwordEncoder.encode("password")).thenReturn("password");
        //WHEN
        ResponseEntity responseEntity = authController.register(userRequest);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testRegisterFailUserExist() {
        //GIVEN
        createAuthController();
        UserRequest userRequest = new UserRequest().setLogin("tintin").setPassword("password");
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new User().setLogin("tintin").setPassword("password"));
        when(typedQuery.setParameter("login", "tintin")).thenReturn(typedQuery);
        doThrow(new DataIntegrityViolationException("test")).when(em).persist(any());
        //WHEN
        ResponseEntity responseEntity = authController.register(userRequest);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().isError());

    }
}
