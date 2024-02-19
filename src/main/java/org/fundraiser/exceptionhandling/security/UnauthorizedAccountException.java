package org.fundraiser.exceptionhandling.security;

import lombok.Getter;

@Getter
public class UnauthorizedAccountException extends RuntimeException {

    private final String username;

    public UnauthorizedAccountException(String username) {
        this.username = username;
    }

}
