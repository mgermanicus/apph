package com.viseo.apph;

import com.viseo.apph.controller.AdminController;
import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.ERole;
import com.viseo.apph.domain.Role;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.UserResponse;
import com.viseo.apph.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.viseo.apph.utils.Utils.inject;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AdminTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery<User> typedQueryUser;
    @Mock
    PasswordEncoder passwordEncoder;

    UserService userService;
    AdminController adminController;

    private void createAdminController() {
        UserDao userDao = new UserDao();
        FolderDao folderDao = new FolderDao();
        inject(userDao, "em", em);
        inject(folderDao, "em", em);
        userService = new UserService();
        inject(userService, "userDao", userDao);
        inject(userService, "folderDao", folderDao);
        adminController = new AdminController();
        inject(adminController, "userService", userService);
        inject(userService, "encoder", passwordEncoder);
    }

    @Test
    public void testGetUserList() {
        //GIVEN
        createAdminController();
        User user = new User().setLogin("Robert");
        List<User> userList = new ArrayList<>();
        userList.add(user);
        when(em.createQuery("SELECT user FROM User user JOIN user.roles role where role.name = 'ROLE_USER'", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.getResultList()).thenReturn(userList);
        //WHEN
        ResponseEntity<List<UserResponse>> responseEntity = adminController.getUserList();
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        List<UserResponse> responseList = responseEntity.getBody();
        assert responseList != null;
        Assert.assertEquals(1, responseList.size());
        Assert.assertEquals(user.getLogin(), responseList.get(0).getLogin());
        Assert.assertEquals(user.getFirstname(), responseList.get(0).getFirstname());
        Assert.assertEquals(user.getLastname(), responseList.get(0).getLastname());
    }
}
