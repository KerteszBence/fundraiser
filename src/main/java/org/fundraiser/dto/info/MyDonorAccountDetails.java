package org.fundraiser.dto.info;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.fundraiser.config.AccountType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class MyDonorAccountDetails {

    private String username;

    private String email;

    private String paypalEmail;

    private AccountType accountType;

    private LocalDateTime lastLogin;

    private List<DonationListItem> outgoingDonations = new ArrayList();

    private List<FollowAccountDetail> followings = new ArrayList();

}