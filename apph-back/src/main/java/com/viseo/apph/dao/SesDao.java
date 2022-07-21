package com.viseo.apph.dao;


import com.viseo.apph.service.FolderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Repository
public class SesDao {
    Logger logger = LoggerFactory.getLogger(SesDao.class);

    @Autowired
    SesClient sesClient;

    public void sendEmail(String sender, String recipient, String subject, String bodyHTML) {
        Destination destination = Destination.builder()
                .toAddresses(recipient)
                .build();
        Content content = Content.builder()
                .data(bodyHTML)
                .build();
        Content sub = Content.builder()
                .data(subject)
                .build();
        Body body = Body.builder()
                .html(content)
                .build();
        Message msg = Message.builder()
                .subject(sub)
                .body(body)
                .build();

        SendEmailRequest request = SendEmailRequest.builder()
                .destination(destination)
                .message(msg)
                .source(sender)
                .build();
        try {
            sesClient.sendEmail(request);
        } catch (SesException e) {
            logger.error(e.awsErrorDetails().errorMessage());
        }
    }
}
