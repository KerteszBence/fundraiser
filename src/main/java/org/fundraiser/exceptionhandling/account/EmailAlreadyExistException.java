package org.fundraiser.exceptionhandling.account;

import lombok.Getter;

@Getter
public class EmailAlreadyExistException extends RuntimeException{

    private final String email;

    public EmailAlreadyExistException(String email) {
        this.email = email;
    }

}