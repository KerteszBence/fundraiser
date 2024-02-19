package org.fundraiser.service;

import com.cloudinary.Cloudinary;
import lombok.extern.slf4j.Slf4j;
import org.fundraiser.domain.FileRegistry;
import org.fundraiser.dto.create.FileRegistryInfo;
import org.fundraiser.dto.create.FileResource;
import org.fundraiser.event.FileDeletionEvent;
import org.fundraiser.exceptionhandling.cloudinary.FileRegistryNotFoundException;
import org.fundraiser.repository.UploadRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public abstract class FileUploaderService {
    private final AccountService accountService;
    private final CampaignService campaignService;
    private final UploadRepository uploadRepository;
    private final Cloudinary cloudinary;
    private final ModelMapper modelMapper;

    @Autowired
    public FileUploaderService(AccountService accountService, CampaignService campaignService, UploadRepository uploadRepository, Cloudinary cloudinary, ModelMapper modelMapper) {
        this.accountService = accountService;
        this.campaignService = campaignService;
        this.uploadRepository = uploadRepository;
        this.cloudinary = cloudinary;
        this.modelMapper = modelMapper;
    }


    public Long processFile(CommonsMultipartFile commonsMultipartFile, String title, String category, Long account_id, Long campaign_id) throws IOException {
        FileRegistry fileRegistry = storeFile(commonsMultipartFile, category);
        fileRegistry.setTitle(title);
        fileRegistry.setCategory(category);
        if (account_id != null) {
            fileRegistry.setAccount(accountService.findAccountById(account_id));
        }
        if (campaign_id != null) {
            fileRegistry.setCampaign(campaignService.findCampaignById(campaign_id));
        }
        Long id = uploadRepository.save(fileRegistry).getFileRegistryId();

        return id;
    }

    protected abstract FileRegistry storeFile(CommonsMultipartFile commonsMultipartFile, String category) throws IOException;

    public abstract FileResource getFile(Long id);

    public FileRegistry findFileById(Long fileId) {
        Optional<FileRegistry> fileOptional = uploadRepository.findById(fileId);
        if (fileOptional.isEmpty()) {
            throw new FileRegistryNotFoundException(fileId);
        }
        return fileOptional.get();
    }

    public List<FileRegistryInfo> getFileRegistryList() {
        return uploadRepository.findAll().stream()
                .map(fileRegistry -> modelMapper.map(fileRegistry, FileRegistryInfo.class))
                .collect(Collectors.toList());
    }

    @EventListener
    public void accountDeleteFiles(FileDeletionEvent event) throws IOException {
        log.info("File deletion is started");
        List<FileRegistry> files = uploadRepository.getFilesByAccount(event.getAccount());
        for (FileRegistry file : files) {
            deleteFile(file.getFileRegistryId());
        }
        log.info("File deletion is complete");
    }

    public abstract void deleteFile(Long resourceId) throws IOException;

    public void deleteFilefromDB(Long fileId) {
        FileRegistry fileRegistry = findFileById(fileId);
        uploadRepository.delete(fileRegistry);
    }

}
