package com.viseo.apph.controller;

import com.viseo.apph.domain.User;
import com.viseo.apph.dto.EmailRequest;
import com.viseo.apph.dto.IResponseDto;
import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.exception.MaxSizeExceededException;
import com.viseo.apph.exception.UnauthorizedException;
import com.viseo.apph.security.Utils;
import com.viseo.apph.service.PhotoService;
import com.viseo.apph.service.SesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "${front-server}")
@RequestMapping("/email")
public class EmailController {
    @Autowired
    Utils utils;
    @Autowired
    SesService sesService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/sendAttachment")
    public ResponseEntity<IResponseDto> sendAttachment(@RequestBody EmailRequest emailRequest) {
        User user = utils.getUser();
        try {
            return ResponseEntity.ok(sesService.SendMessageAttachment(user, emailRequest));
        } catch (MaxSizeExceededException msee) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new MessageResponse(msee.getMessage()));
        } catch (UnauthorizedException | IOException ue) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(ue.getMessage()));
        } catch (MessagingException me) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(me.getMessage()));
        }
    }
}
