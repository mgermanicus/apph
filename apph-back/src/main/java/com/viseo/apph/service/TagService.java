package com.viseo.apph.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.viseo.apph.dao.TagDAO;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import io.jsonwebtoken.Claims;
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
    UserDAO userDao;

    @Autowired
    TagDAO tagDAO;

    @Transactional
    public List<Tag> getTags(Claims claims) {
        User user = userDao.getUserByLogin(claims.get("login").toString());
        return tagDAO.getTagsByUser(user.getId());
    }

    @Transactional
    public Tag createTag(String tagName, Claims claims) {
        User user = userDao.getUserByLogin(claims.get("login").toString());
        Tag tag = new Tag().setName(tagName).setUser(user);
        return tagDAO.createTag(tag);
    }

    @Transactional
    public Set<Tag> createListTags(String listOfTags, User user) throws JsonProcessingException {
        // Parse tags from json to Tags[]
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Tag[] parsedTags = gson.fromJson(listOfTags, Tag[].class);
        List<Tag> tagsToCreate = Arrays.stream(parsedTags).filter(tag -> tag.getId() == 0).collect(Collectors.toList());
        List<Tag> tags = Arrays.stream(parsedTags).filter(tag -> tag.getId() != 0).collect(Collectors.toList());
        Set<Tag> allTags = new HashSet<>(tags);
        for (Tag tag : tagsToCreate) {
            Tag newTag = tagDAO.createTag(new Tag().setName(tag.getName().substring(12)).setUser(user));
            allTags.add(newTag);
            allTags.size();
        }
        return allTags;
    }
}
