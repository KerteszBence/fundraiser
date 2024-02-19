package org.fundraiser.dto.info;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.fundraiser.config.AccountType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class MyCreatorAccountDetails {

    private String username;

    private String email;

    private Long likeCounter;

    private String paypalEmail;

    private AccountType accountType;

    private LocalDateTime lastLogin;

    private List<MyCampaignInfo> openCampaigns;

    private List<MyCampaignInfo> closedCampaigns;

    private List<DonationListItem> outgoingDonations = new ArrayList();

    private List<DonationListItem> incomingDonations = new ArrayList();

    private List<FollowAccountDetail> followers = new ArrayList();

    private List<FollowAccountDetail> followings = new ArrayList();

}
