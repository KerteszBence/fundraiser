package org.fundraiser.exceptionhandling.campaign;

import lombok.Getter;

@Getter
public class CampaignNotFoundByAccountException extends RuntimeException {

    private final String username;

    public CampaignNotFoundByAccountException(String username) {
        this.username = username;
    }

}
