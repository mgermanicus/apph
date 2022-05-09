package com.viseo.apph;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.controller.TagController;
import com.viseo.apph.dao.TagDAO;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.service.TagService;
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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TagTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery typedQuery;

    TagService tagService;
    TagController tagController;

    private void createTagController() {
        TagDAO tagDAO = new TagDAO();
        inject(tagDAO, "em", em);
        UserDAO userDAO = new UserDAO();
        inject(userDAO, "em", em);
        tagService = new TagService();
        inject(tagService, "tagDAO", tagDAO);
        inject(tagService, "userDAO", userDAO);
        tagController = new TagController();
        inject(tagController, "tagService", tagService);
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
    public void testGetTags() {
        //GIVEN
        createTagController();
        User user = (User) new User().setLogin("toto").setPassword("toto_pwd").setId(1);
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        Tag tag1 = new Tag().setUser(user).setName("tag1");
        List<Tag> tags = new ArrayList<>();
        tags.add(tag1);
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("login", "toto")).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(user);
        when(em.createQuery("SELECT t FROM Tag t WHERE t.user.id=:userId", Tag.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", 1L)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(tags);
        //WHEN
        ResponseEntity<IResponseDTO> responseEntity = tagController.getTags(jws);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testFailTokenExpired() {
        //GIVEN
        createTagController();
        User user = new User().setLogin("toto").setPassword("totoPwd");
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis())).signWith(JwtConfig.getKey()).compact();
        //WHEN
        ResponseEntity responseEntity = tagController.getTags(jws);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testFailUserNotFind() {
        //GIVEN
        createTagController();
        String jws = Jwts.builder().claim("login", "dumb_toto").setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        //WHEN
        ResponseEntity responseEntity = tagController.getTags(jws);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void testFailWrongSignature() {
        //GIVEN
        createTagController();
        User user = new User().setLogin("toto").setPassword("totoPwd");
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis())).signWith(key).compact();
        //WHEN
        ResponseEntity responseEntity = tagController.getTags(jws);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }
}
