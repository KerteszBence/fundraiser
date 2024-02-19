package org.fundraiser.exceptionhandling.account;

import lombok.Getter;

@Getter
public class CreatorAccountNotFindByUserNameException extends RuntimeException {

    private final String userName;

    public CreatorAccountNotFindByUserNameException(String userName) {
        this.userName = userName;
    }

}
