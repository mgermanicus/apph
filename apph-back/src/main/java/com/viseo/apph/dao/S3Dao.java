package com.viseo.apph.dao;

import com.viseo.apph.exception.InvalidFileException;
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

    public File convertMultiPartToFile(MultipartFile file, String name) throws IOException, InvalidFileException {
        if (name != null) {
            File convertedFile = new File(name);
            try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
                fos.write(file.getBytes());
                return convertedFile;
            }
        } else {
            throw new InvalidFileException("Le nom est null");
        }
    }

    public String upload(MultipartFile file, String name) throws InvalidFileException, IOException {
        if (file != null) {
            File fileToSave = convertMultiPartToFile(file, name);
            PutObjectResponse por = s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucketName).key(user + fileToSave.getName())
                    .contentLength(fileToSave.length()).build(), RequestBody.fromFile(fileToSave));
            if (fileToSave.exists()) {
                Files.delete(Paths.get(fileToSave.getAbsolutePath()));
            }
            return por.eTag();
        } else {
            throw new InvalidFileException("Fichier invalide");
        }
    }

    public byte[] download(String filename) {
        ResponseBytes<GetObjectResponse> s3Object = s3Client.getObject(
                GetObjectRequest.builder().bucket(bucketName).key(user + filename).build(),
                ResponseTransformer.toBytes());
        return s3Object.asByteArray();
    }

    public String delete(String filename) {
        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(user + filename).build());
        return filename + " supprimé";
    }
}
