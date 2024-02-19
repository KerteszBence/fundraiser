package org.fundraiser.exceptionhandling.account;

import lombok.Getter;

@Getter
public class UserNameAlreadyExistException extends RuntimeException {

    private final String username;

    public UserNameAlreadyExistException(String username) {
        this.username = username;
    }

}
