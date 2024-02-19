package org.fundraiser.event;

import lombok.Getter;
import org.fundraiser.domain.Account;
import org.springframework.context.ApplicationEvent;

@Getter
public class DonationRefundCreatorEvent extends ApplicationEvent {

    private Account account;

    public DonationRefundCreatorEvent(Object source, Account account) {
        super(source);
        this.account = account;
    }

}
