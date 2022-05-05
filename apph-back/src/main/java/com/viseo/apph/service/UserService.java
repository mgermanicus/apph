package com.viseo.apph.service;

import com.viseo.apph.dao.FolderDAO;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.User;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.dto.UserRequest;
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

    @Autowired
    FolderDAO folderDAO;

    PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public void registerUser(UserRequest userRequest) {
        User newUser = new User().setLogin(userRequest.getLogin()).setPassword(encoder.encode(userRequest.getPassword())).setFirstname(userRequest.getFirstName()).setLastname(userRequest.getLastName());
        userDAO.createUser(newUser);
        Folder rootFolder = new Folder().setName(newUser.getFirstname()).setParentFolderId(null).setUser(newUser);
        folderDAO.createFolder(rootFolder);
    }

    @Transactional
    public User login(UserRequest userRequest) throws IllegalArgumentException {
        User user = userDAO.getUserByLogin(userRequest.getLogin());
        if (encoder.matches(userRequest.getPassword(), user.getPassword()))
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
        if (userDAO.existByLogin(newLogin))
            throw new DataIntegrityViolationException("Login is Already in Use.");
        userDAO.editLogin(userId, newLogin);
    }

    @Transactional
    public void editFirstname(long userId, String newFirstname) throws NotFoundException {
        if (newFirstname == null) return;
        userDAO.editFirstname(userId, newFirstname);
    }
    @Transactional
    public void editLastname(long userId, String newLastname) throws NotFoundException {
        if (newLastname == null) return;
        userDAO.editLastname(userId, newLastname);
    }

    @Transactional
    public void editPassword(long userId, String newPassword) throws NotFoundException {
        if (newPassword == null) return;
        userDAO.editPassword(userId, encoder.encode(newPassword));
    }

    public User getUserById(long id) {
        return userDAO.getUserById(id);
    }
}
