package com.viseo.apph.service;

import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.RoleDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.ERole;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Role;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.UserRequest;
import com.viseo.apph.dto.UserResponse;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    RoleDao roleDao;

    @Autowired
    FolderDao folderDao;

    @Autowired
    JwtUtils jwtUtils;

    PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public void registerUser(UserRequest userRequest) {
        if (userRequest.getLastName().length() > 127 || userRequest.getFirstName().length() > 127)
            throw new IllegalArgumentException("signup.error.nameOverChar");
        if (userRequest.getLogin().length() > 255)
            throw new IllegalArgumentException("signup.error.emailOverChar");
        Set<Role> set = new HashSet<>();
        Role roleUser = roleDao.getRole(ERole.ROLE_USER);
        set.add(roleUser);
        User newUser = new User().setLogin(userRequest.getLogin()).setPassword(encoder.encode(userRequest.getPassword())).setFirstname(userRequest.getFirstName()).setLastname(userRequest.getLastName()).setRoles(set);
        userDao.createUser(newUser);
        Folder rootFolder = new Folder().setName(newUser.getFirstname() + " " + newUser.getLastname()).setParentFolderId(null).setUser(newUser);
        folderDao.createFolder(rootFolder);
    }

    @Transactional
    public String editUser(User user, UserRequest request, String token) throws NotFoundException {
        User userEntity = userDao.getUserByLogin(user.getLogin());
        String newToken = token.substring(7); //Remove Bearer :
        Map<String, String> newClaims = new HashMap<>();
        Folder rootFolder = folderDao.getParentFolderByUser(userEntity);
        if (userEntity == null) throw new NotFoundException("");
        if (request.getFirstName() != null) {
            if (request.getFirstName().length() > 127)
                throw new IllegalArgumentException("signup.error.nameOverChar");
            userEntity.setFirstname(request.getFirstName());
            newClaims.put("firstname", request.getFirstName());
            rootFolder.setName(userEntity.getFirstname() + " " + userEntity.getLastname());
        }
        if (request.getLastName() != null) {
            if (request.getLastName().length() > 127)
                throw new IllegalArgumentException("signup.error.nameOverChar");
            userEntity.setLastname(request.getLastName());
            newClaims.put("lastname", request.getLastName());
            rootFolder.setName(userEntity.getFirstname() + " " + userEntity.getLastname());
        }
        if (request.getPassword() != null)
            userEntity.setPassword(encoder.encode(request.getPassword()));
        if (request.getLogin() != null) {
            if (request.getLogin().length() > 255)
                throw new IllegalArgumentException("signup.error.loginOverChar");
            if (userDao.existByLogin(request.getLogin()))
                throw new DataIntegrityViolationException("signup.error.emailUsed");
            userEntity.setLogin(request.getLogin());
            newClaims.put("login", request.getLogin());
        }
        return jwtUtils.setClaimOnToken(newToken, newClaims);
    }
    
    @Transactional
    public List<UserResponse> getUserList() {
        return userDao.getUserList().stream().map((user) -> new UserResponse()
                .setLogin(user.getLogin())
                .setFirstname(user.getFirstname())
                .setLastname(user.getLastname())).collect(Collectors.toList());
    }
}
