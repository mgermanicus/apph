package com.example.apphback.controller;

import com.example.apphback.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static java.net.HttpURLConnection.HTTP_OK;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    S3Service s3s;

    @PostMapping("upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        return s3s.save(file);
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
