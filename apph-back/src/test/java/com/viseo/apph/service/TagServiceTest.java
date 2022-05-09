package com.viseo.apph.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viseo.apph.dao.TagDao;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceTest {
    @Mock
    EntityManager em;

    TagService tagService;

    private void createPhotoService() {
        TagDao tagDao = new TagDao();
        setEntityManager(tagDao, em);
        tagService = new TagService();
        tagService.tagDao = tagDao;
    }

    void setEntityManager(Object dao, EntityManager em) {
        try {
            Field emField = dao.getClass().getDeclaredField("em");
            emField.setAccessible(true);
            emField.set(dao, em);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateListTags() throws JsonProcessingException {
        //GIVEN
        createPhotoService();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        User user = new User().setLogin("toto").setPassword("toto_pwd");
        Tag tag1 = new Tag().setName("+ Add New Tag tag_1");
        Tag tag2 = new Tag().setName("+ Add New Tag tag_2");
        List<Tag> list = new ArrayList<>();
        list.add(tag1);
        list.add(tag2);
        String listOfTags = gson.toJson(list);
        //WHEN
        tagService.createListTags(listOfTags, user);
        //THEN
        verify(em, times(2)).persist(any(Tag.class));
    }
}
