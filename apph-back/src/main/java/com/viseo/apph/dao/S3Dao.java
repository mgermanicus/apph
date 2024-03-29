package com.viseo.apph.dao;

import com.viseo.apph.domain.Photo;
import org.apache.commons.codec.digest.MurmurHash2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Repository
public class S3Dao {
    @Value("${bucketName}")
    String bucketName;

    @Value("${s3user}")
    String user;

    @Autowired
    S3Client s3Client;

    private String hashString(String string) {
        return String.valueOf(MurmurHash2.hash32(string));
    }

    public File convertMultiPartToFile(MultipartFile file, String name) throws IOException {
        File convertedFile = new File(name);
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
            return convertedFile;
        }
    }

    public String upload(MultipartFile file, Photo photo) throws IOException {
        String name = hashString(String.valueOf(photo.getId())) + photo.getFormat();
        File fileToSave = convertMultiPartToFile(file, name);
        PutObjectResponse por = s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName).key(user + fileToSave.getName())
                .contentLength(fileToSave.length()).build(), RequestBody.fromFile(fileToSave));
        if (fileToSave.exists()) {
            Files.delete(Paths.get(fileToSave.getAbsolutePath()));
        }
        return por.eTag();
    }

    public String getPhotoUrl(Photo photo) {
        String encodedId = hashString(String.valueOf(photo.getId()));
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(user + encodedId + photo.getFormat())).toExternalForm();
    }

    public byte[] download(Photo photo) {
        String name = hashString(String.valueOf(photo.getId())) + photo.getFormat();
        ResponseBytes<GetObjectResponse> s3Object = s3Client.getObject(
                GetObjectRequest.builder().bucket(bucketName).key(user + name).build(),
                ResponseTransformer.toBytes());
        return s3Object.asByteArray();
    }

    public String delete(Photo photo) {
        String name = hashString(String.valueOf(photo.getId())) + photo.getFormat();
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(user + name).build());
        return name + " supprimé";
    }
}
