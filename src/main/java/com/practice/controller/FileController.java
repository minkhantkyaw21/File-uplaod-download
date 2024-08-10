package com.practice.controller;

import com.practice.entity.FileEntity;
import com.practice.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FileController {
    @Autowired
    private FileRepository fileRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFilename(file.getOriginalFilename());
            fileEntity.setContentType(file.getContentType());
            fileEntity.setData(file.getBytes());
            fileRepository.save(fileEntity);
//            String message="File successfully uploaded";
//            HttpStatus httpStatus=HttpStatus.OK;
//            return new ResponseEntity<>(message,httpStatus);
            return ResponseEntity.ok().body(Map.of("message", "File successfully uploaded"));

        } catch (IOException e) {
           return  ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileEntity>> getAllFiles() {
        List<FileEntity> files=fileRepository.findAll();
        return  ResponseEntity.ok(files);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> doownloadFile(@PathVariable Long id) {
        FileEntity fileEntity=fileRepository.findById(id).orElse(null);
        if(fileEntity !=null) {
            HttpHeaders httpHeaders=new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(fileEntity.getContentType()));
            httpHeaders.setContentDisposition(ContentDisposition.attachment().filename(fileEntity.getFilename()).build());
            ByteArrayResource byteArrayResource=new ByteArrayResource(fileEntity.getData());

            return  ResponseEntity.ok().headers(httpHeaders).body(byteArrayResource);

        }else{
            return ResponseEntity.notFound().build();
        }
    }
}
