package com.viseo.apph.controller;

import com.viseo.apph.dto.MessageResponse;
import com.viseo.apph.dto.ResponseDTO;
import com.viseo.apph.exception.InvalidFileException;
import com.viseo.apph.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    S3Service s3s;

    @PostMapping("/upload")
    public ResponseEntity<ResponseDTO> upload(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(new MessageResponse(s3s.save(file)));
        } catch (IOException | InvalidFileException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable("filename") String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", MediaType.ALL_VALUE);
        headers.add("Content-Disposition", "attachment; filename=" + filename);
        byte[] bytes = s3s.download(filename);
        return ResponseEntity.status(HTTP_OK).headers(headers).body(bytes);
    }

    @DeleteMapping("/{filename}")
    public String delete(@PathVariable("filename") String filename) {
        return s3s.delete(filename);
    }
}
