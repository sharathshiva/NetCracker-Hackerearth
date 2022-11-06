package com.hackerearth.controller;

import com.hackerearth.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "https://net-cracker-hackerearth.herokuapp.com/")
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private static final Logger logger= LoggerFactory.getLogger(FileController.class);

    @Autowired
    private S3Service s3Service;

    //Upload of the file
    @PostMapping(value = "/uploadFile",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String addFile(@RequestParam("files")MultipartFile file) throws IOException {
        long fileSize = file.getSize()/1024;
        if(file.getContentType().endsWith("pdf")){
            return s3Service.saveFile(file);
        }else if(fileSize > 1000){
            throw new MultipartException("File Size must be less than 1MB");
        }else{
            throw new MultipartException("Not PDF file");
        }
    }

    //Get URL of all files in bucket
    @GetMapping("/listFiles")
    public List<String> getAllFiles(){
        return s3Service.listAllFiles();
    }

}
