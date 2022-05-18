package com.viseo.apph;

import com.viseo.apph.controller.TagController;
import com.viseo.apph.dao.TagDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.TagService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static com.viseo.apph.utils.Utils.inject;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TagTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery typedQuery;
    @Mock
    Utils utils;
    TagService tagService;
    TagController tagController;

    private void createTagController() {
        TagDao tagDao = new TagDao();
        inject(tagDao, "em", em);
        UserDao userDao = new UserDao();
        inject(userDao, "em", em);
        tagService = new TagService();
        inject(tagService, "tagDao", tagDao);
        inject(tagService, "userDao", userDao);
        tagController = new TagController();
        inject(tagController, "tagService", tagService);
        inject(tagController, "utils", utils);
    }

    @Test
    public void testGetTags() {
        //GIVEN
        createTagController();
        User user = (User) new User().setLogin("toto").setPassword("toto_pwd").setId(1);
        when(utils.getUser()).thenReturn(user);
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
        ResponseEntity<IResponseDto> responseEntity = tagController.getTags();
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }
}
