package com.viseo.apph.service;

import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.RoleDao;
import com.viseo.apph.dao.SesDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.ERole;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Role;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.LoginRequest;
import com.viseo.apph.dto.UserListResponse;
import com.viseo.apph.dto.UserRequest;
import com.viseo.apph.dto.UserResponse;
import com.viseo.apph.exception.ExpiredLinkException;
import com.viseo.apph.exception.InvalidTokenException;
import com.viseo.apph.exception.NotFoundException;
import com.viseo.apph.security.JwtUtils;
import com.viseo.apph.utils.FrontServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
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
    SesService sesService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    SesDao sesDao;

    @Autowired
    FrontServer frontServer;

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
        sesService.sendVerifyRegister(userRequest.getLogin());
        User newUser = new User().setLogin(userRequest.getLogin()).setPassword(encoder.encode(userRequest.getPassword())).setFirstname(userRequest.getFirstName()).setLastname(userRequest.getLastName()).setRoles(set).setIsActive(false);
        userDao.createUser(newUser);
        Folder rootFolder = new Folder().setName(newUser.getFirstname() + " " + newUser.getLastname()).setParentFolderId(null).setUser(newUser);
        folderDao.createFolder(rootFolder);
    }

    @Transactional
    public void forgotPassword(String login, String language) throws NoResultException {
        User user = userDao.getUserByLogin(login);
        String token = jwtUtils.generateJwtToken(login, 1_800_000);
        user.setResetting(true);
        user.setTokenForResetting(token);
        String bodyHTMLEn = "<html>" + "<head></head>" + "<body>" + "<h1>Password Forgotten APPH</h1>"
                + "<p> Dear APPH Customer.</p><br>"
                + "<p>Please click on the button to reset your password</p><br>"
                + "<a href=\"" + frontServer.getFrontServer() + "/resetPassword?token=" + token + "\"><button>Reset your password</button></a>"
                + "<p>This link will be valid for 30 minutes</p><br>"
                + "<p>If you did not make this request, please ignore the email</p><br>"
                + "</body>" + "</html>";
        String bodyHTMLFr = "<html>" + "<head></head>" + "<body>" + "<h1>Mot de passe oublié APPH !</h1>"
                + "<p>Chère client APPH</p><br>"
                + "<p>Veuillez cliquer sur le bouton ci-dessous afin de réinitialiser votre mot de passe</p><br>"
                + "<a href=\"" + frontServer.getFrontServer() + "/resetPassword?token=" + token + "\"><button>Reset your password</button></a><br>"
                + "<p>Ce lien sera valide 30 min</p><br>"
                + "<p>Si vous n'êtes pas à l'origine de cette requête, veuillez ignorer l'email</p><br>"
                + "</body>" + "</html>";
        String bodyHTML = language.equals("fr") ? bodyHTMLFr : bodyHTMLEn;
        sesDao.sendEmail("wassim.bouhtout@viseo.com", user.getLogin(), "Reset your password", bodyHTML);
    }

    public User checkToken(String token) throws InvalidTokenException, ExpiredLinkException {
        if (!jwtUtils.isSignatureValid(token))
            throw new InvalidTokenException("token.signatureNotValid");
        if (!jwtUtils.isTokenNotExpired(token))
            throw new InvalidTokenException("token.isExpired");
        String login = jwtUtils.getUserNameFromJwtToken(token);
        User user = userDao.getUserByLogin(login);
        if (!user.getIsActive()) return user;
        if (!user.isResetting() || !token.equals(user.getTokenForResetting()))
            throw new ExpiredLinkException();
        return user;
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

    @Transactional
    public void resetPassword(String token, String newPassword) throws NoResultException {
        String login = jwtUtils.getUserNameFromJwtToken(token);
        userDao.resetPassword(login, encoder.encode(newPassword));
    }

    @Transactional
    public UserListResponse addContact(User user, UserRequest request) {
        User contact = userDao.getUserByLogin(request.getLogin());
        User userEntity = userDao.getUserByLogin(user.getLogin());
        userEntity.addContact(contact);
        UserListResponse response = new UserListResponse();
        response.setUserList(userEntity.getContacts().stream().map((userContact) -> new UserResponse()
                .setLogin(userContact.getLogin())
                .setFirstname(userContact.getFirstname())
                .setLastname(userContact.getLastname())).collect(Collectors.toList()));
        return response;
    }

    @Transactional
    public UserListResponse getContacts(User user) {
        User userEntity = userDao.getUserByLogin(user.getLogin());
        UserListResponse response = new UserListResponse();
        response.setUserList(userEntity.getContacts().stream().map((userContact) -> new UserResponse()
                .setLogin(userContact.getLogin())
                .setFirstname(userContact.getFirstname())
                .setLastname(userContact.getLastname())).collect(Collectors.toList()));
        return response;
    }

    @Transactional
    public boolean verifyUserVerified(LoginRequest loginRequest) {
        if (!userDao.getUserByLogin(loginRequest.getLogin()).getIsActive()) {
            sesService.sendVerifyRegister(loginRequest.getLogin());
            return false;
        }
        return true;
    }

    @Transactional
    public String activeUser(String token) throws InvalidTokenException, ExpiredLinkException {
        User user = checkToken(token);
        if (user != null) {
            user.setIsActive(true);
            return "user.redirectionToLogin3s";
        }
        return "user.errorActivateUser";
    }
}
