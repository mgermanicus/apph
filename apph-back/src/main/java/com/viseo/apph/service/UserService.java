package com.viseo.apph.service;

import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.User;
import com.viseo.apph.exception.NotFoundException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;
    PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public void registerUser(String login, String password) {
        User newUser = new User().setLogin(login).setPassword(password);
        userDAO.createUser(newUser);
    }

    @Transactional
    public User login(String login, String password) throws IllegalArgumentException {
        User user = userDAO.getUserByLogin(login);
        if (encoder.matches(password, user.getPassword()))
            return user;
        throw new IllegalArgumentException();
    }

    @Transactional
    public User getUser(Claims claims) {
        String login = claims.get("login").toString();
        return userDAO.getUserByLogin(login);
    }

    @Transactional
    public void editLogin(long userId, String newLogin) throws DataIntegrityViolationException, NotFoundException {
        if (newLogin == null) return;
        if (!userDAO.existById(userId))
            throw new NotFoundException("User not Found.");
        if (userDAO.existByLogin(newLogin))
            throw new DataIntegrityViolationException("Login is Already in Use.");
        // TODO revoke token & generate new one
        userDAO.editLogin(userId, newLogin);
    }

    @Transactional
    public void editFirstname(long userId, String newFirstname) throws NotFoundException {
        if (newFirstname == null) return;
        if (!userDAO.existById(userId))
            throw new NotFoundException("User not Found.");
        userDAO.editFirstname(userId, newFirstname);
    }
    @Transactional
    public void editLastname(long userId, String newLastname) throws NotFoundException {
        if (newLastname == null) return;
        if (!userDAO.existById(userId))
            throw new NotFoundException("User not Found.");
        userDAO.editLastname(userId, newLastname);
    }

    @Transactional
    public void editPassword(long userId, String newPassword) throws NotFoundException {
        if (newPassword == null) return;
        if (!userDAO.existById(userId))
            throw new NotFoundException("User not Found.");
        userDAO.editPassword(userId, newPassword);
    }

}
