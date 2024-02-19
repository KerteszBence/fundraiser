package org.fundraiser.exceptionhandling.campaign;

import lombok.Getter;

@Getter
public class CampaignNotFoundByCategoryException extends RuntimeException {

    private final String category;

    public CampaignNotFoundByCategoryException(String category) {
        this.category = category;
    }

}