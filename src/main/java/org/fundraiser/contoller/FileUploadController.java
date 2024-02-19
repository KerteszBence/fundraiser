package org.fundraiser.contoller;

import lombok.extern.slf4j.Slf4j;
import org.fundraiser.dto.create.FileRegistryInfo;
import org.fundraiser.dto.create.FileResource;
import org.fundraiser.dto.create.FormDataWithFile;
import org.fundraiser.service.FileUploaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileUploadController {

    private final FileUploaderService fileUploaderService;

    @Autowired
    public FileUploadController(FileUploaderService fileUploaderService) {
        this.fileUploaderService = fileUploaderService;
    }

    @PostMapping("/withoutFormData")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<Long> uploadFile(@RequestParam("file") @NotNull @NotBlank CommonsMultipartFile file) throws IOException {
        return new ResponseEntity<>(fileUploaderService.processFile(file, null, null, null, null), HttpStatus.OK);
    }

    @PostMapping("/withFormData")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<Long> uploadFileWithFormData(@ModelAttribute FormDataWithFile data) throws IOException {
        System.out.println(data.toString());
        return new ResponseEntity<>(fileUploaderService.processFile(data.getFile(), data.getTitle(), data.getCategory(), data.getAccount_id(), data.getCampaign_id()),
                HttpStatus.OK);
    }

    @GetMapping("/downloadFileList")
    public List<FileRegistryInfo> downloadFile() {
        return fileUploaderService.getFileRegistryList();
    }

    @GetMapping("/downloadFile/{resourceId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long resourceId) {
        FileResource fileResource = fileUploaderService.getFile(resourceId);
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(fileResource.getMediaType()))
                .body(fileResource.getData());
    }

    @DeleteMapping("/deleteFile/{fileId}")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        try {
            fileUploaderService.deleteFile(fileId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("File deleted successfully.");
    }

}
