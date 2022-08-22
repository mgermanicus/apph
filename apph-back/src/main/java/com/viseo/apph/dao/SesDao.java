package com.viseo.apph.dao;


import com.viseo.apph.domain.User;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

@Repository
public class SesDao {
    Logger logger = LoggerFactory.getLogger(SesDao.class);

    @Autowired
    SesClient sesClient;

    public IResponseDto sendEmailWithAttachment(String sender, String recipient, String subject, String bodyHTML, byte[] file) throws MessagingException {
        try {
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session);
            message.setSubject(subject, "UTF-8");
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(recipient));
            MimeMultipart msgBody = new MimeMultipart("alternative");
            MimeBodyPart wrap = new MimeBodyPart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8");
            msgBody.addBodyPart(htmlPart);
            wrap.setContent(msgBody);
            MimeMultipart msg = new MimeMultipart("mixed");
            message.setContent(msg);
            msg.addBodyPart(wrap);
            // Attachment
            MimeBodyPart att = new MimeBodyPart();
            DataSource fds = new ByteArrayDataSource(file, "application/zip");
            att.setDataHandler(new DataHandler(fds));
            String reportName = subject + ".zip";
            att.setFileName(reportName);
            // Add the attachment to the message.
            msg.addBodyPart(att);
            // Send message
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());
            byte[] arr = new byte[buf.remaining()];
            buf.get(arr);
            SdkBytes data = SdkBytes.fromByteArray(arr);
            RawMessage rawMessage = RawMessage.builder()
                    .data(data)
                    .build();
            SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
                    .rawMessage(rawMessage)
                    .build();
            sesClient.sendRawEmail(rawEmailRequest);
            return new MessageResponse("email.success.sentWithAttachment");
        } catch (MessagingException | IOException | MessageRejectedException e) {
            logger.error(e.getMessage());
            throw new MessagingException("email.error.sentWithAttachment");
        }
    }

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
