package org.fundraiser.event;

import lombok.Getter;
import org.fundraiser.domain.Campaign;
import org.springframework.context.ApplicationEvent;

@Getter
public class CampaignDeletionEvent extends ApplicationEvent {

    private Campaign campaign;

    public CampaignDeletionEvent(Object source, Campaign campaign) {
        super(source);
        this.campaign = campaign;
    }

}
