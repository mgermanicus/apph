package com.viseo.apph;

import com.viseo.apph.config.multipartConfig.UpdatableMultipartConfigElement;
import com.viseo.apph.controller.AdminController;
import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.SettingDao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Setting;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.SettingRequest;
import com.viseo.apph.dto.SettingResponse;
import com.viseo.apph.dto.UserResponse;
import com.viseo.apph.service.SettingService;
import com.viseo.apph.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static com.viseo.apph.utils.Utils.inject;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AdminTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery<User> typedQueryUser;
    @Mock
    TypedQuery<Setting> typedQuerySetting;
    @Mock
    PasswordEncoder passwordEncoder;

    UpdatableMultipartConfigElement updatableMultipartConfigElement;
    UserService userService;
    SettingService settingService;
    AdminController adminController;

    private void createAdminController() {
        UserDao userDao = new UserDao();
        FolderDao folderDao = new FolderDao();
        SettingDao settingDao = new SettingDao();
        inject(userDao, "em", em);
        inject(folderDao, "em", em);
        inject(settingDao, "em", em);
        userService = new UserService();
        inject(userService, "userDao", userDao);
        inject(userService, "folderDao", folderDao);
        settingService = new SettingService();
        updatableMultipartConfigElement = new UpdatableMultipartConfigElement("", 0, 0, 1);
        inject(settingService, "settingDao", settingDao);
        inject(settingService, "updatableMultipartConfigElement", updatableMultipartConfigElement);
        adminController = new AdminController();
        inject(adminController, "userService", userService);
        inject(adminController, "settingService", settingService);
        inject(userService, "encoder", passwordEncoder);
    }

    @Test
    public void testGetUserList() {
        //GIVEN
        createAdminController();
        User user = new User().setLogin("Robert");
        List<User> userList = new ArrayList<>();
        userList.add(user);
        when(em.createQuery("SELECT user FROM User user JOIN user.roles role where role.name = 'ROLE_USER'", User.class)).thenReturn(typedQueryUser);
        when(typedQueryUser.getResultList()).thenReturn(userList);
        //WHEN
        ResponseEntity<List<UserResponse>> responseEntity = adminController.getUserList();
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        List<UserResponse> responseList = responseEntity.getBody();
        assert responseList != null;
        Assert.assertEquals(1, responseList.size());
        Assert.assertEquals(user.getLogin(), responseList.get(0).getLogin());
        Assert.assertEquals(user.getFirstname(), responseList.get(0).getFirstname());
        Assert.assertEquals(user.getLastname(), responseList.get(0).getLastname());
    }

    @Test
    public void testGetSetting() {
        //GIVEN
        createAdminController();
        when(em.createQuery("SELECT setting from Setting setting", Setting.class)).thenReturn(typedQuerySetting);
        when(typedQuerySetting.getSingleResult()).thenReturn(new Setting().setDownloadSize(10).setUploadSize(20));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = adminController.getSettings();
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        SettingResponse response = (SettingResponse) responseEntity.getBody();
        assert response != null;
        Assert.assertEquals(10, response.getDownloadSize());
        Assert.assertEquals(20, response.getUploadSize());
    }

    @Test
    public void testUpdateSettings() {
        //GIVEN
        createAdminController();
        SettingRequest request = new SettingRequest().setDownloadSize(1).setUploadSize(2);
        when(em.createQuery("SELECT setting from Setting setting", Setting.class)).thenReturn(typedQuerySetting);
        when(typedQuerySetting.getSingleResult()).thenReturn(new Setting().setDownloadSize(10).setUploadSize(20));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = adminController.updateSettings(request);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        SettingResponse response = (SettingResponse) responseEntity.getBody();
        assert response != null;
        Assert.assertEquals("OK", response.getMessage());
    }
}
