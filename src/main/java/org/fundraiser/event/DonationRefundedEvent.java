package org.fundraiser.event;

import lombok.Getter;
import org.fundraiser.domain.Account;
import org.fundraiser.domain.Donation;
import org.springframework.context.ApplicationEvent;

@Getter
public class DonationRefundedEvent extends ApplicationEvent {

    private Donation donation;

    private Account account;

    public DonationRefundedEvent(Object source, Donation donation, Account account) {
        super(source);
        this.donation = donation;
        this.account = account;
    }

}