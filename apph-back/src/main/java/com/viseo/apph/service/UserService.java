package com.viseo.apph.service;

import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.ERole;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Role;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.UserRequest;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    FolderDao folderDao;

    PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public void registerUser(UserRequest userRequest) {
        Set<Role> set = new HashSet<Role>();
        set.add((new Role(ERole.ROLE_USER)));
        User newUser = new User().setLogin(userRequest.getLogin()).setPassword(encoder.encode(userRequest.getPassword())).setFirstname(userRequest.getFirstName()).setLastname(userRequest.getLastName()).setRoles(set);
        userDao.createUser(newUser);
        Folder rootFolder = new Folder().setName(newUser.getFirstname()).setParentFolderId(null).setUser(newUser);
        folderDao.createFolder(rootFolder);
    }

    @Transactional
    public User login(UserRequest userRequest) throws IllegalArgumentException {
        User user = userDao.getUserByLogin(userRequest.getLogin());
        if (encoder.matches(userRequest.getPassword(), user.getPassword()))
            return user;
        throw new IllegalArgumentException();
    }

    @Transactional
    public User getUser(Claims claims) {
        String login = claims.get("login").toString();
        return userDao.getUserByLogin(login);
    }

    @Transactional
    public User getUserById(long id) {
        return userDao.getUserById(id);
    }
}
