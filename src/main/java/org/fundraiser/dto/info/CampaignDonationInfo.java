package org.fundraiser.dto.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fundraiser.domain.Account;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignDonationInfo {

    private Double balance;

    private Account source;

    private Account target;

    private LocalDateTime transactionTime;

    private Boolean isSubscription;

}