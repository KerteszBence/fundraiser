package org.fundraiser.exceptionhandling.account;

import lombok.Getter;

@Getter
public class AccountNotFindByUserNameException extends RuntimeException {

    private final String userName;

    public AccountNotFindByUserNameException(String userName) {
        this.userName = userName;
    }

}
