package com.viseo.apph.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
        try {
            File fileToSave = convertMultiPartToFile(file);
            PutObjectResponse por = s3.putObject(PutObjectRequest.builder()
                    .bucket(bucketName).key(user + filename)
                    .contentType(MediaType.APPLICATION_PDF.toString())
                    .contentLength((long) fileToSave.length()).build(), RequestBody.fromFile(fileToSave));
            return por.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] download(String filename) {
        try {
            ResponseBytes<GetObjectResponse> s3Object = s3.getObject(
                    GetObjectRequest.builder().bucket(bucketName).key(user + filename).build(),
                    ResponseTransformer.toBytes());
            return s3Object.asByteArray();
        } catch (SdkServiceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String delete(String filename) {
        try {
            s3.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(user + filename).build());
            return filename + " deleted";
        } catch (SdkServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }
}
