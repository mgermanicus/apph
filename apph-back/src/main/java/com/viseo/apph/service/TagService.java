package com.viseo.apph.service;

import com.viseo.apph.dao.TagDAO;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.InvalidObjectException;

@Service
public class TagService {
    @Autowired
    UserDAO userDao;

    @Autowired
    TagDAO tagDAO;

    @Transactional
    public String createTag(Tag tag) throws InvalidObjectException {
        return tagDAO.createTag(tag);
    }
}
