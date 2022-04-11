package com.example.apphback.service;

import org.springframework.web.multipart.MultipartFile;

public interface IAmazonS3 {

    String save(MultipartFile file);

    byte[] download(String filename);

    String delete(String filename);
}
