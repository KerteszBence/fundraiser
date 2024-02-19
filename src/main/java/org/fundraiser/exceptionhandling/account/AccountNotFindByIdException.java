package org.fundraiser.exceptionhandling.account;

import lombok.Getter;

@Getter
public class AccountNotFindByIdException extends RuntimeException {

    private final Long id;

    public AccountNotFindByIdException(Long id) {
        this.id = id;
    }

}
