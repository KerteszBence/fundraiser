package org.fundraiser.exceptionhandling.cloudinary;

import lombok.Getter;

@Getter
public class FileRegistryNotFoundException extends RuntimeException {

    private final Long id;

    public FileRegistryNotFoundException(Long id) {
        this.id = id;
    }

}