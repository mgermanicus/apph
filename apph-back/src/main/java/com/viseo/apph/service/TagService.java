package com.viseo.apph.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viseo.apph.dao.TagDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService {
    @Autowired
    UserDao userDao;

    @Autowired
    TagDao tagDao;

    @Transactional
    public List<Tag> getTags(User user) {
        return tagDao.getTagsByUser(user.getId());
    }

    @Transactional
    public Set<Tag> createListTags(String listOfTags, User user) {
        // Parse tags from json to Tags[]
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Tag[] parsedTags = gson.fromJson(listOfTags, Tag[].class);
        List<Tag> tagsToCreate = Arrays.stream(parsedTags).filter(tag -> tag.getId() == 0).collect(Collectors.toList());
        if (Boolean.FALSE.equals(isTagValid(tagsToCreate)))
            throw new IllegalArgumentException("photo.error.tagOverChar");
        Set<Tag> allTags = Arrays.stream(parsedTags).filter(tag -> tag.getId() != 0).collect(Collectors.toSet());
        for (Tag tag : tagsToCreate) {
            Tag newTag = tagDao.createTag(new Tag().setName(tag.getName().substring(14)).setUser(user));
            allTags.add(newTag);
        }
        return allTags;
    }

    Boolean isTagValid(List<Tag> list) {
        for (Tag tag : list) {
            if (tag.getName().substring(14).length() > 255)
                return false;
        }
        return true;
    }
}
