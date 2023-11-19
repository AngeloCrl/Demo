package com.app.demo.files;

import com.app.demo.utils.exception.CustomException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

public class UploadDownloadUtil {
    private static final Logger logger = LoggerFactory.getLogger(UploadDownloadUtil.class);
    private Path foundFile;

    public static String encodeFilename(String filename) {
        try {
            return URLEncoder.encode(filename, StandardCharsets.UTF_8);
        } catch (CustomException e) {
            e.printStackTrace();
            return filename;
        }
    }

    public static String saveFile(String fileName, MultipartFile multipartFile) throws IOException {
        logger.info("Uploading - Saving File(s)");
        Path uploadPath = Paths.get("uploaded-files");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileCode = RandomStringUtils.randomAlphanumeric(8);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileCode + "-" + fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            logger.error("Exception While Uploading - Saving File(s)");
            throw new IOException("Could not save file: " + fileName, ioe);
        }
        return fileCode;
    }

    public Resource getFileAsResource(String fileCode) throws IOException {
        logger.info("Downloading File(s)");
        try (Stream<Path> stream = Files.list(Paths.get("uploaded-files"))) {
            stream.forEach(file -> {
                if (file.getFileName().toString().startsWith(fileCode)) {
                    foundFile = file;
                }
            });
        }
        if (foundFile != null) {
            return new UrlResource(foundFile.toUri());
        }
        return null;
    }
}
