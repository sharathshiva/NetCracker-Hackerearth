package com.hackerearth.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 s3;

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    public String saveFile(MultipartFile file) throws IOException {
        String originalName = "dummy" + UUID.randomUUID().toString().split("-")[0].substring(0,7) + "."+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        File file1 = convertMultipartFileToFile(file);
        s3.putObject(new PutObjectRequest(bucketName, originalName, file1).withCannedAcl(CannedAccessControlList.PublicRead));
        file1.delete();
        return s3.getUrl(bucketName,originalName).toString();
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fileOutputStream = new FileOutputStream(convFile);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();
        return convFile;
    }

    public List<String> listAllFiles() {
        List<String> fileLinks = new ArrayList<String>();
        ListObjectsV2Result listObjectsV2Result = s3.listObjectsV2(bucketName);
        List<String> fileList = listObjectsV2Result.getObjectSummaries().stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
        for(String file: fileList){
            fileLinks.add(s3.getUrl(bucketName,file).toString());
        }
        return fileLinks;
    }

}
