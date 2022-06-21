package com.viseo.apph;

import com.viseo.apph.controller.AuthController;
import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.RoleDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.ERole;
import com.viseo.apph.domain.Role;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.LoginRequest;
import com.viseo.apph.dto.UserRequest;
import com.viseo.apph.security.JwtUtils;
import com.viseo.apph.security.UserDetailsImpl;
import com.viseo.apph.service.UserService;
import net.bytebuddy.utility.RandomString;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;

import static com.viseo.apph.utils.Utils.inject;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AuthTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery<Role> typedQueryRole;
    @Mock
    TypedQuery<User> typedQueryUser;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationManager authenticationManager;
    AuthController authController;

    private void createAuthController() {
        UserDao userDao = new UserDao();
        FolderDao folderDao = new FolderDao();
        RoleDao roleDao = new RoleDao();
        inject(userDao, "em", em);
        inject(folderDao, "em", em);
        inject(roleDao, "em", em);
        UserService userService = new UserService();
        inject(userService, "encoder", passwordEncoder);
        inject(userService, "userDao", userDao);
        inject(userService, "folderDao", folderDao);
        inject(userService, "roleDao", roleDao);
        authController = new AuthController();
        inject(authController, "userService", userService);
        inject(authController, "authenticationManager", authenticationManager);
        inject(authController, "jwtUtils", new JwtUtils());
    }

    @Test
    public void testLoginUser() {
        testLogin(ERole.ROLE_USER);
    }

    @Test
    public void testLoginAdmin() {
        testLogin(ERole.ROLE_ADMIN);
    }

    public void testLogin(ERole role) {
        //GIVEN
        createAuthController();
        Set<Role> set = new HashSet<>();
        set.add(new Role(role));
        User user = (User) new User().setLogin("tintin")
                .setPassword("P@ssw0rd")
                .setLastname("test")
                .setFirstname("test")
                .setRoles(set)
                .setId(1);
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        SecurityContext securityContext = mock(SecurityContext.class);
        MockedStatic<SecurityContextHolder> staticMock = mockStatic(SecurityContextHolder.class);
        staticMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        LoginRequest loginRequest = new LoginRequest().setLogin("tintin").setPassword("P@ssw0rd");
        //WHEN
        ResponseEntity<String> responseEntity = authController.login(loginRequest);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        staticMock.close();
    }

    @Test
    public void testRegister() {
        //GIVEN
        createAuthController();
        UserRequest userRequest = new UserRequest().setLogin("tintin").setPassword("password").setFirstName("toto").setLastName("tintin");
        when(em.createQuery("SELECT r FROM Role r WHERE r.name=:name", Role.class)).thenReturn(typedQueryRole);
        when(typedQueryRole.setParameter("name", ERole.ROLE_USER)).thenReturn(typedQueryRole);
        when(typedQueryRole.getSingleResult()).thenReturn(new Role(ERole.ROLE_USER));
        when(passwordEncoder.encode("password")).thenReturn("password");
        //WHEN
        ResponseEntity<String> responseEntity = authController.register(userRequest);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testRegisterFailUserExist() {
        //GIVEN
        createAuthController();
        UserRequest userRequest = new UserRequest().setLogin("tintin").setPassword("password").setFirstName("toto").setLastName("tintin");
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.getSingleResult()).thenReturn(new User().setLogin("tintin").setPassword("password"));
        when(typedQueryUser.setParameter("login", "tintin")).thenReturn(typedQueryUser);
        when(em.createQuery("SELECT r FROM Role r WHERE r.name=:name", Role.class)).thenReturn(typedQueryRole);
        when(typedQueryRole.setParameter("name", ERole.ROLE_USER)).thenReturn(typedQueryRole);
        when(typedQueryRole.getSingleResult()).thenReturn(new Role(ERole.ROLE_USER));
        doThrow(new DataIntegrityViolationException("test")).when(em).persist(any());
        //WHEN
        ResponseEntity<String> responseEntity = authController.register(userRequest);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().isError());
    }

    @Test
    public void testRegisterWithIllegalNameLength() {
        //GIVEN
        createAuthController();
        String name = RandomString.make(128);
        UserRequest userRequest = new UserRequest().setFirstName(name).setLastName(name);
        //WHEN
        ResponseEntity<String> responseEntity = authController.register(userRequest);
        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals("signup.error.nameOverChar", responseEntity.getBody());
    }

    @Test
    public void testRegisterWithIllegalLoginLength() {
        //GIVEN
        createAuthController();
        String login = RandomString.make(256);
        UserRequest userRequest = new UserRequest().setFirstName("toto").setLastName("tintin").setLogin(login);
        //WHEN
        ResponseEntity<String> responseEntity = authController.register(userRequest);
        //THEN
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals("signup.error.emailOverChar", responseEntity.getBody());
    }
}
