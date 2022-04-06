package com.example.apphback.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service implements IAmazonS3 {

    @Value("${bucketName}")
    private String bucketName;

    private final AmazonS3 s3;

    public S3Service(AmazonS3 s3) {
        this.s3 = s3;
    }

    /**
     * save a file to S3 bucket
     *
     * @param file file to save
     * @return response
     */
    @Override
    public String save(MultipartFile file) {
        String filename = file.getOriginalFilename();
        try {
            File fileToSave = convertMultiPartToFile(file);
            PutObjectResult por = s3.putObject(bucketName, filename, fileToSave);
            return por.getContentMd5();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * download a file with its file name
     *
     * @param filename name of file to be downloaded
     * @return file to download
     */
    @Override
    public byte[] download(String filename) {
        S3Object s3o = s3.getObject(bucketName, filename);
        S3ObjectInputStream objContent = s3o.getObjectContent();
        try {
            return IOUtils.toByteArray(objContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * delete a file with its file name
     *
     * @param filename name of the file to be deleted
     * @return success delete message
     */
    @Override
    public String delete(String filename) {
        s3.deleteObject(bucketName, filename);
        return filename + " deleted";
    }

    /**
     * show all files
     *
     * @return
     */
    @Override
    public List<String> listAll() {
        ListObjectsV2Result listObjectsV2Result = s3.listObjectsV2(bucketName);
        return listObjectsV2Result.getObjectSummaries().stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }

    /**
     * convert MultipartFile to File
     *
     * @param file file to be converted
     * @return file converted
     * @throws IOException IOException
     */
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }
}