package com.viseo.apph;

import com.viseo.apph.controller.EmailController;
import com.viseo.apph.dao.*;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.Setting;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.EmailRequest;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.FolderService;
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
import java.util.Arrays;

import static com.viseo.apph.utils.Utils.inject;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SesTest {
    @Mock
    EntityManager em;
    @Mock
    EntityManager emFolder;
    @Mock
    Utils utils;
    @Mock
    SesClient sesClient;
    @Mock
    S3Client s3Client;
    @Mock
    TypedQuery<Setting> typedQuerySetting;
    @Mock
    TypedQuery<Photo> typedQueryPhoto;
    @Mock
    TypedQuery<Folder> typedQueryFolder;

    EmailController emailController;

    private void createEmailController() {
        S3Dao s3Dao = new S3Dao();
        PhotoDao photoDao = new PhotoDao();
        SettingDao settingDao = new SettingDao();
        FolderDao folderDao = new FolderDao();
        inject(photoDao, "em", em);
        inject(settingDao, "em", em);
        inject(folderDao, "em", emFolder);
        s3Client = mock(S3Client.class, RETURNS_DEEP_STUBS);
        inject(s3Dao, "s3Client", s3Client);
        PhotoService photoService = new PhotoService();
        inject(photoService, "photoDao", photoDao);
        inject(photoService, "s3Dao", s3Dao);
        inject(photoService, "settingDao", settingDao);
        SesService sesService = new SesService();
        SesDao sesDao = new SesDao();
        FolderService folderService = new FolderService();
        inject(folderService, "folderDao", folderDao);
        inject(folderService, "photoDao", photoDao);
        inject(folderService, "settingDao", settingDao);
        inject(folderService, "s3Dao", s3Dao);
        sesClient = mock(SesClient.class, RETURNS_DEEP_STUBS);
        inject(sesDao, "sesClient", sesClient);
        inject(sesService, "sesDao", sesDao);
        inject(sesService, "photoService", photoService);
        inject(sesService, "folderService", folderService);
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
        EmailRequest emailRequest = new EmailRequest().setContent("Content").setRecipient("min.sun@viseo.com").setSubject("Test").setIds(ids).setType("photo");
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
    public void testSendAttachmentFolder() {
        createEmailController();
        long[] ids = {1L};
        User user = (User) new User().setLogin("min.sun@viseo.com").setId(2);
        Folder folder = (Folder) new Folder().setUser(user).setId(ids[0]);
        Photo photo = (Photo) new Photo().setFormat("jpg").setTitle("title").setUser(user).setFolder(folder).setId(1L);
        Photo photo2 = (Photo) new Photo().setFormat("jpg").setTitle("title").setUser(user).setFolder(folder).setId(2L);
        EmailRequest emailRequest = new EmailRequest().setContent("Content").setRecipient("min.sun@viseo.com").setSubject("Test").setIds(ids).setType("folder");
        GetObjectResponse response = mock(GetObjectResponse.class);
        ResponseBytes<GetObjectResponse> s3Object = ResponseBytes.fromByteArray(response, "".getBytes(StandardCharsets.UTF_8));
        when(utils.getUser()).thenReturn(user);
        when(em.find(any(), anyLong())).thenReturn(photo);
        when(emFolder.find(any(), anyLong())).thenReturn(folder);
        when(s3Client.getObject(any(GetObjectRequest.class), eq(ResponseTransformer.toBytes()))).thenReturn(s3Object);
        when(em.createQuery("SELECT setting from Setting setting", Setting.class)).thenReturn(typedQuerySetting);
        when(typedQuerySetting.getSingleResult()).thenReturn(new Setting().setDownloadSize(5).setUploadSize(5));
        when(em.createQuery("SELECT p FROM Photo p WHERE p.folder =: folder", Photo.class)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.setParameter("folder", folder)).thenReturn(typedQueryPhoto);
        when(typedQueryPhoto.getResultList()).thenReturn(Arrays.asList(photo, photo2));
        when(emFolder.createQuery("SELECT folder from Folder folder WHERE folder.parentFolderId = :parentId", Folder.class)).thenReturn(typedQueryFolder);
        when(typedQueryFolder.setParameter("parentId", folder.getId())).thenReturn(typedQueryFolder);
        when(typedQueryFolder.getResultList()).thenReturn(null);
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
        EmailRequest emailRequest = new EmailRequest().setContent("Content").setRecipient("min.sun@viseo.com").setSubject("Test").setIds(ids).setType("photo");
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
        EmailRequest emailRequest = new EmailRequest().setContent("Content").setRecipient("min.sun@viseo.com").setSubject("Test").setIds(ids).setType("photo");
        //WHEN
        ResponseEntity<IResponseDto> responseEntity = emailController.sendAttachment(emailRequest);
        //THEN
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }
}
