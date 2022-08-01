package com.viseo.apph.service;

import com.viseo.apph.dao.SesDao;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.PhotoResponse;
import com.viseo.apph.dto.PhotosRequest;
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

    public IResponseDto SendMessageAttachment(User user, String recipient, String subject, String content, long[] ids) throws MaxSizeExceededException, UnauthorizedException, IOException {
        String bodyHTML = "<html>" + "<head></head>" + "<body>"
                + "<h1>" + subject + "</h1>"
                + "<p>" + content + "</p>"
                + "</body>" + "</html>";
        PhotoResponse photoResponse = photoService.downloadZip(user, new PhotosRequest().setIds(ids));
        return sesDao.sendEmailWithAttachment(user.getLogin(), recipient, subject, bodyHTML, photoResponse.getData());
    }
}
