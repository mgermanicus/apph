package com.viseo.apph.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class S3Service implements IAmazonS3 {

    @Value("${bucketName}")
    String bucketName;

    @Value("${user}")
    String user;

    final S3Client s3;

    public S3Service(S3Client s3) {
        this.s3 = s3;
    }

    @Override
    public String save(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return upload(file, filename);
    }

    public String saveWithName(MultipartFile file,String name) {
        return upload(file, name);
    }

    public String upload(MultipartFile file, String name) {
        try {
            File fileToSave = convertMultiPartToFile(file,name);
            // contentType(MediaType.<Type>.toString()) can be used to change the format
            PutObjectResponse por = s3.putObject(PutObjectRequest.builder()
                    .bucket(bucketName).key(user + name)
                    .contentLength(fileToSave.length()).build(), RequestBody.fromFile(fileToSave));
            if(fileToSave.exists()){
                Files.delete(Paths.get(fileToSave.getAbsolutePath()));
            }
            return por.eTag();
        } catch (IOException e) {
            throw new FileSystemNotFoundException("File not found");
        }
    }

    @Override
    public byte[] download(String filename) {
        ResponseBytes<GetObjectResponse> s3Object = s3.getObject(
        GetObjectRequest.builder().bucket(bucketName).key(user + filename).build(),
        ResponseTransformer.toBytes());
        return s3Object.asByteArray();
    }

    @Override
    public String delete(String filename) {
        s3.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(user + filename).build());
        return filename + " deleted";
    }

    public File convertMultiPartToFile(MultipartFile file, String name) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(name));
        try(FileOutputStream fos = new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
            return convertedFile;
        }
    }
}
