package org.fundraiser.exceptionhandling.donation;

import lombok.Getter;

@Getter
public class DonationNotFindByIdException extends RuntimeException {

    private final Long id;

    public DonationNotFindByIdException(Long id) {
        this.id = id;
    }

}
