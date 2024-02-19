package org.fundraiser.exceptionhandling.account;

import lombok.Getter;

@Getter
public class AccountNotFindByEmailException extends RuntimeException {

    private final String email;

    public AccountNotFindByEmailException(String email) {
        this.email = email;
    }

}
