package org.fundraiser.exceptionhandling.campaign;

import lombok.Getter;

@Getter
public class CampaignNotFindByIdException extends RuntimeException {

    private final Long id;

    public CampaignNotFindByIdException(Long id) {
        this.id = id;
    }

}
