package org.fundraiser.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import hu.progmasters.backend.service.UploadResponse;
import lombok.Data;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "media")
@Data
public class FileRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long fileRegistryId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "file_path")
    private String filePath;
    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "title")
    private String title;

    @Column(name = "category")
    private String category;

    @Column(name = "upload_datetime")
    @JsonFormat(locale = "hu", shape = JsonFormat.Shape.STRING, pattern = "yyyy. MM. dd. HH:mm:ss (Z)")
    private ZonedDateTime uploadDateTime = ZonedDateTime.now();
    public FileRegistry() {
    }

    public FileRegistry(UploadResponse uploadResponse, CommonsMultipartFile commonsMultipartFile) {
        this.filePath = uploadResponse.getSecureUrl();
        this.originalFileName = commonsMultipartFile.getFileItem().getName();
        this.fileSize = uploadResponse.getBytes();
        this.mediaType = commonsMultipartFile.getContentType();
    }

    public FileRegistry(String fullFilePath, long size, String contentType, String originalFileName) {
        this.filePath = fullFilePath;
        this.fileSize = size;
        this.mediaType = contentType;
        this.originalFileName = originalFileName;
    }
}