package org.fundraiser.event;

import lombok.Getter;
import org.fundraiser.domain.Account;
import org.fundraiser.domain.Donation;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class DonationRefundCampaignEvent extends ApplicationEvent {

    private List<Donation> donation;

    private Account account;

    public DonationRefundCampaignEvent(Object source, List<Donation> donation, Account account) {
        super(source);
        this.donation = donation;
        this.account = account;
    }

}
