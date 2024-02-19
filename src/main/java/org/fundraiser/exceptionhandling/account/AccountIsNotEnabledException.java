package org.fundraiser.exceptionhandling.account;

import lombok.Getter;

@Getter
public class AccountIsNotEnabledException extends RuntimeException {
    private final String userName;

    public AccountIsNotEnabledException(String userName) {
        this.userName = userName;
    }

}
