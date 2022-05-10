package com.viseo.apph.service;

import com.viseo.apph.config.JwtConfig;
import com.viseo.apph.dao.FolderDAO;
import com.viseo.apph.dao.UserDAO;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.User;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.dto.UserRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Key;

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
    public String editUser(long userId, UserRequest request, Claims claims) throws NotFoundException {
        User user = userDAO.getUserById(userId);
        Key key = JwtConfig.getKey();
        JwtBuilder newClaims = Jwts.builder().setClaims(claims);
        if (user == null) throw new NotFoundException("");
        if (request.getFirstName() != null) {
            user.setFirstname(request.getFirstName());
            newClaims.claim("firstname", request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastname(request.getLastName());
            newClaims.claim("lastname", request.getLastName());
        }
        if (request.getPassword() != null)
            user.setPassword(encoder.encode(request.getPassword()));
        if (request.getLogin() != null) {
            if (userDAO.existByLogin(request.getLogin()))
                throw new DataIntegrityViolationException("");
            user.setLogin(request.getLogin());
            newClaims.claim("login", request.getLogin());
        }
        return newClaims.setExpiration(claims.getExpiration()).signWith(key).compact();
    }
}
