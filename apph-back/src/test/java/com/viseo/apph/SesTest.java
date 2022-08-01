package com.viseo.apph;

import com.viseo.apph.controller.EmailController;
import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.dao.SesDao;
import com.viseo.apph.dao.SettingDao;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.Setting;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.EmailRequest;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.SesService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.ses.SesClient;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.nio.charset.StandardCharsets;

import static com.viseo.apph.utils.Utils.inject;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SesTest {
    @Mock
    EntityManager em;
    @Mock
    Utils utils;
    @Mock
    SesClient sesClient;
    @Mock
    S3Client s3Client;
    @Mock
    TypedQuery<Setting> typedQuerySetting;

    EmailController emailController;

    private void createEmailController() {
        S3Dao s3Dao = new S3Dao();
        PhotoDao photoDao = new PhotoDao();
        SettingDao settingDao = new SettingDao();
        inject(photoDao, "em", em);
        inject(settingDao, "em", em);
        s3Client = mock(S3Client.class, RETURNS_DEEP_STUBS);
        inject(s3Dao, "s3Client", s3Client);
        PhotoService photoService = new PhotoService();
        inject(photoService, "photoDao", photoDao);
        inject(photoService, "s3Dao", s3Dao);
        inject(photoService, "settingDao", settingDao);
        SesService sesService = new SesService();
        SesDao sesDao = new SesDao();
        sesClient = mock(SesClient.class, RETURNS_DEEP_STUBS);
        inject(sesDao, "sesClient", sesClient);
        inject(sesService, "sesDao", sesDao);
        inject(sesService, "photoService", photoService);
        emailController = new EmailController();
        inject(emailController, "sesService", sesService);
        inject(emailController, "utils", utils);
    }

    @Test
    public void testSendAttachment() {
        createEmailController();
        long[] ids = {1L, 1L};
        User user = (User) new User().setLogin("min.sun@viseo.com").setId(2);
        Photo photo = (Photo) new Photo().setFormat("jpg").setTitle("title").setUser(user).setId(ids[0]);
        EmailRequest emailRequest = new EmailRequest().setContent("Content").setRecipient("min.sun@viseo.com").setSubject("Test").setIds(ids);
        GetObjectResponse response = mock(GetObjectResponse.class);
        ResponseBytes<GetObjectResponse> s3Object = ResponseBytes.fromByteArray(response, "".getBytes(StandardCharsets.UTF_8));
        when(utils.getUser()).thenReturn(user);
        when(em.find(any(), anyLong())).thenReturn(photo);
        when(s3Client.getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()))).thenReturn(s3Object);
        when(em.createQuery("SELECT setting from Setting setting", Setting.class)).thenReturn(typedQuerySetting);
        when(typedQuerySetting.getSingleResult()).thenReturn(new Setting().setDownloadSize(5).setUploadSize(5));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = emailController.sendAttachment(emailRequest);
        //THEN
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testSendAttachmentTooLarge() {
        createEmailController();
        long[] ids = {1L, 1L};
        User user = (User) new User().setLogin("min.sun@viseo.com").setId(2);
        Photo photo = (Photo) new Photo().setFormat("jpg").setTitle("title").setUser(user).setId(ids[0]);
        EmailRequest emailRequest = new EmailRequest().setContent("Content").setRecipient("min.sun@viseo.com").setSubject("Test").setIds(ids);
        GetObjectResponse response = mock(GetObjectResponse.class);
        ResponseBytes<GetObjectResponse> s3Object = ResponseBytes.fromByteArray(response, "".getBytes(StandardCharsets.UTF_8));
        when(utils.getUser()).thenReturn(user);
        when(em.find(any(), anyLong())).thenReturn(photo);
        when(s3Client.getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()))).thenReturn(s3Object);
        when(em.createQuery("SELECT setting from Setting setting", Setting.class)).thenReturn(typedQuerySetting);
        when(typedQuerySetting.getSingleResult()).thenReturn(new Setting().setDownloadSize(0).setUploadSize(0));
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = emailController.sendAttachment(emailRequest);
        //THEN
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, responseEntity.getStatusCode());
    }

    @Test
    public void testSendAttachmentUnauthorized() {
        createEmailController();
        long[] ids = {1L, 1L};
        EmailRequest emailRequest = new EmailRequest().setContent("Content").setRecipient("min.sun@viseo.com").setSubject("Test").setIds(ids);
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = emailController.sendAttachment(emailRequest);
        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }
}
