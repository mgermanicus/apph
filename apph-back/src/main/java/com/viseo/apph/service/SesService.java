package com.viseo.apph.service;

import com.viseo.apph.dao.SesDao;
import com.viseo.apph.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SesService {
    @Autowired
    SesDao sesDao;

    public String sendVerifyRegister(User user) {
        String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello please !</h1>"
                + "<p> Dear APPH Customer.</p><br>"
                + "<p>Please click on the button to complete the verification process</p><br>"
                + "<a href=\"http://stackoverflow.com\"><button>VERIFY YOUR EMAIL ADDRESS</button></a>"
                + "</body>" + "</html>";
        sesDao.sendEmail("min.sun@viseo.com", user.getLogin(), "VERIFY EMAIL ADDRESS", bodyHTML);
        return "ok send";
    }
}
