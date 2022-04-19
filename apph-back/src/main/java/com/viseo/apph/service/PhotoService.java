package com.viseo.apph.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class PhotoService {

    public String addPhoto(MultipartFile filePhoto) throws IOException {
        String filePath = "photos";
        File dirPhoto = new File(filePath);
        if(!dirPhoto.exists()){
            if(!dirPhoto.mkdirs()){
                throw new IOException("Unable to create new directory");
            }
        }
        String fileName;
        if ( (fileName = filePhoto.getOriginalFilename())!=null){
            filePhoto.transferTo(new File(dirPhoto.getAbsolutePath() + fileName));
        }
        return fileName;
    }
}
