package com.viseo.apph.service;

import com.viseo.apph.dao.SesDao;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.*;
import com.viseo.apph.exception.MaxSizeExceededException;
import com.viseo.apph.exception.UnauthorizedException;
import com.viseo.apph.security.JwtUtils;
import com.viseo.apph.utils.FrontServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SesService {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    SesDao sesDao;

    @Autowired
    PhotoService photoService;

    @Autowired
    FolderService folderService;

    @Autowired
    FrontServer frontServer;

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

    public void sendVerifyRegister(String login) {
        String token = jwtUtils.generateJwtToken(login, 1_800_000);
        String bodyHTML = "<html> <head></head> <body> <h1>Hello please !</h1>"
                + "<p> Dear APPH Customer.</p><br>"
                + "<p>Please click on the button to complete the verification process</p><br>"
                + "<a href=" + frontServer.getFrontServer() + "/auth/activateUser?token=" + token + ">"
                + "<button>VERIFY YOUR EMAIL ADDRESS</button>"
                + "</a>"
                + "</body></html>";
        sesDao.sendEmail("min.sun@viseo.com", login, "VERIFY EMAIL ADDRESS", bodyHTML);
    }

    public void sendDeleteUser(String email) {
        String bodyHTML = "<html> <head></head> <body> <h1>Hello please !</h1>"
                + "<p> Dear APPH Customer.</p><br>"
                + "<p>Your account has been successfully deleted.</p><br>"
                + "</body></html>";
        sesDao.sendEmail("min.sun@viseo.com", email, "Your APPH account was deleted", bodyHTML);
    }
}
