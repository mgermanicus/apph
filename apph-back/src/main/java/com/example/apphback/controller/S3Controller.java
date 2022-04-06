package com.example.apphback.controller;

import com.example.apphback.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class S3Controller {

    @Autowired
    private S3Service s3s;

    /**
     * Save file
     *
     * @param file file to saved
     * @return response from S3Service
     */
    @PostMapping("upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        return s3s.save(file);
    }

    /**
     * download a file
     *
     * @param filename name of file to be downloaded
     * @return file to download
     */
    @GetMapping("download/{filename}")
    public byte[] download(@RequestParam("filename") String filename) {
        return s3s.download(filename);
    }

    /**
     * delete a file
     *
     * @param filename name of file to be deleted
     * @return delete message
     */
    @DeleteMapping("{filename}")
    public String delete(@RequestParam("filename") String filename) {
        return s3s.delete(filename);
    }

    /**
     * get all files
     *
     * @return all files
     */
    @GetMapping()
    public List<String> getAllFiles() {
        return s3s.listAll();
    }
}
