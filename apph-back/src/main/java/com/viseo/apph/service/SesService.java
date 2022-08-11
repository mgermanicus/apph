package com.viseo.apph.service;

import com.viseo.apph.dao.SesDao;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.*;
import com.viseo.apph.exception.MaxSizeExceededException;
import com.viseo.apph.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SesService {
    @Autowired
    SesDao sesDao;

    @Autowired
    PhotoService photoService;

    @Autowired
    FolderService folderService;

    public IResponseDto SendMessageAttachment(User user, EmailRequest emailRequest) throws MaxSizeExceededException, UnauthorizedException, IOException {
        String bodyHTML = "<html>" + "<head></head>" + "<body>"
                + "<h1>" + emailRequest.getSubject() + "</h1>"
                + "<p>" + emailRequest.getContent() + "</p>"
                + "</body>" + "</html>";
        if (emailRequest.getType().equals("folder")) {
            FolderResponse folderResponse = folderService.downloadFolder(user, new FolderRequest().setId(emailRequest.getIds()[0]));
            return sesDao.sendEmailWithAttachment(user.getLogin(), emailRequest.getRecipient(), emailRequest.getSubject(), bodyHTML, folderResponse.getData());
        } else {
            PhotoResponse photoResponse = photoService.downloadZip(user, new PhotosRequest().setIds(emailRequest.getIds()));
            return sesDao.sendEmailWithAttachment(user.getLogin(), emailRequest.getRecipient(), emailRequest.getSubject(), bodyHTML, photoResponse.getData());
        }
    }
}
