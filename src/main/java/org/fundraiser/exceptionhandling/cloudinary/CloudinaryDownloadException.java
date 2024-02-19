package org.fundraiser.exceptionhandling.cloudinary;

import lombok.Getter;

@Getter
public class CloudinaryDownloadException extends RuntimeException {

    private final Long id;

    public CloudinaryDownloadException(Long id) {
        this.id = id;
    }

}
