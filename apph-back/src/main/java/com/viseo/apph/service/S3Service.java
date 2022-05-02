package com.viseo.apph.service;

import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3Service {

    @Autowired
    S3Dao s3Dao;

    public String save(MultipartFile file) throws InvalidFileException, IOException {
        if (file != null) {
            String filename = file.getOriginalFilename();
            return s3Dao.upload(file, filename);
        }
        throw new InvalidFileException("file must not be null");
    }

    public String saveWithName(MultipartFile file, String name) throws InvalidFileException, IOException {
        return s3Dao.upload(file, name);
    }


    public byte[] download(String filename) {
        return s3Dao.download(filename);
    }

    public String delete(String filename) {
        return s3Dao.delete(filename);
    }
}
