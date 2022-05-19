package com.viseo.apph.service;

import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.RoleDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.ERole;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Role;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.UserRequest;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        Set<Role> set = new HashSet<Role>();
        Role roleUser = roleDao.getRole(ERole.ROLE_USER);
        set.add(roleUser);
        User newUser = new User().setLogin(userRequest.getLogin()).setPassword(encoder.encode(userRequest.getPassword())).setFirstname(userRequest.getFirstName()).setLastname(userRequest.getLastName()).setRoles(set);
        userDao.createUser(newUser);
        Folder rootFolder = new Folder().setName(newUser.getFirstname()).setParentFolderId(null).setUser(newUser);
        folderDao.createFolder(rootFolder);
    }

    @Transactional
    public String editUser(String login, UserRequest request, String token) throws NotFoundException {
        User user = userDao.getUserByLogin(login);
        token = token.substring(7, token.length()); //Remove Bearer :
        Map newClaims = new HashMap<String,String>();
        if (user == null) throw new NotFoundException("");
        if (request.getFirstName() != null) {
            user.setFirstname(request.getFirstName());
            newClaims.put("firstname", request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastname(request.getLastName());
            newClaims.put("lastname", request.getLastName());
        }
        if (request.getPassword() != null)
            user.setPassword(encoder.encode(request.getPassword()));
        if (request.getLogin() != null) {
            if (userDao.existByLogin(request.getLogin()))
                throw new DataIntegrityViolationException("");
            user.setLogin(request.getLogin());
            newClaims.put("login", request.getLogin());
        }
        return jwtUtils.setClaimOnToken(token, newClaims);
    }
}
