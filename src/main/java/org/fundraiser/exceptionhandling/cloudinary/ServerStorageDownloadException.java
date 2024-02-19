package org.fundraiser.exceptionhandling.cloudinary;

import lombok.Getter;

@Getter
public class ServerStorageDownloadException extends RuntimeException {

    private final Long id;

    public ServerStorageDownloadException(Long id) {
        this.id = id;
    }

}