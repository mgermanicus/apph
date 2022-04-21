package com.viseo.apph.service;

import com.viseo.apph.exception.InvalidFileException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IAmazonS3 {

    String save(MultipartFile file) throws InvalidFileException, IOException;

    byte[] download(String filename);

    String delete(String filename);
}
