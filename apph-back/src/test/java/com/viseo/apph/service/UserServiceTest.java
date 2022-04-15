package com.viseo.apph.service;

import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    EntityManager em;

    private void createUserService() {
        UserDAO userDAO = new UserDAO();
        setEntityManager(userDAO, em);
        userService = new UserService();
        userService.userDAO = userDAO;
    }

    void setEntityManager(Object DAO, EntityManager em) {
        try {
            Field emField = DAO.getClass().getDeclaredField("em");
            emField.setAccessible(true);
            emField.set(DAO,em);
        }
        catch(IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    UserService userService;

    @Test
    public void testUserCreation(){
        // Given
        createUserService();
        // WHEN
        userService.registerUser("Wassim","P@ssw0rd");
        // THEN
        verify(em, times(1)).persist(any(User.class));
    }

    @Test
    public void testUserDelete(){
        //GIVEN
        createUserService();
        when(em.find(User.class,1L)).thenReturn(new User());
        //WHEN
        userService.deleteUser(1L);
        //THEN
        verify(em,times(1)).remove(any(User.class));
    }

    @Test
    public void testUserEntity(){
        //GIVEN
        User user = new User().setLogin("Wassim").setPassword("P@ssw0rd");
        //THEN
        Assert.assertEquals("Wassim", user.getLogin());
        Assert.assertEquals("P@ssw0rd", user.getPassword());
    }
}
