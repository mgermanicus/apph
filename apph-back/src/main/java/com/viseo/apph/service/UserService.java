package com.viseo.apph.service;

import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    @Transactional
    public void registerUser(String login, String password) {
        User newUser = new User().setLogin(login).setPassword(password);
        userDAO.createUser(newUser);
    }

    @Transactional
    public void deleteUser(long userId) {
        userDAO.deleteUser(userId);
    }

    @Transactional
    public User getUser(long userId) {
        return userDAO.getUser(userId);
    }
}
