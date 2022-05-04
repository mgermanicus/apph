package com.viseo.apph.service;

import com.viseo.apph.dao.TagDAO;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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
}
