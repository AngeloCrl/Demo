package com.app.demo.files;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/files")
@Api(tags = "Files")
public class FileController {

    @PostMapping("/upload")
    @ApiOperation(value = "FileController.uploadFile")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        long size = multipartFile.getSize();

        String fileCode = UploadDownloadUtil.saveFile(fileName, multipartFile);

        FileUploadResponse response = new FileUploadResponse();
        response.setFileName(fileName);
        response.setSize(size);
        response.setDownloadUri("/files/download/" + fileCode);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/download/{fileCode}")
    @ApiOperation(value = "FileController.downloadFile")
    public ResponseEntity<?> downloadFile(@PathVariable("fileCode") String fileCode) {
        UploadDownloadUtil uploadDownloadUtil = new UploadDownloadUtil();
        Resource resource;
        try {
            resource = uploadDownloadUtil.getFileAsResource(fileCode);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }
        String contentType = "application/octet-stream";
//        String encodedFilename = UploadDownloadUtil.encodeFilename(resource.getFilename());
//        String headerValue = "attachment; filename*=UTF-8''" + encodedFilename;
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}
