package org.fundraiser.utils.mapper;

import org.fundraiser.domain.Account;
import org.fundraiser.domain.Campaign;
import org.fundraiser.dto.info.MyCreatorAccountDetails;

import java.util.stream.Collectors;

public class MyCreatorAccountDetailsMapper {

    public static MyCreatorAccountDetails creatorAccountDetailsMapper(Account account) {

        MyCreatorAccountDetails myCreatorAccountDetails = new MyCreatorAccountDetails();

        myCreatorAccountDetails.setUsername(account.getUsername());

        myCreatorAccountDetails.setEmail(account.getEmail());

        myCreatorAccountDetails.setPaypalEmail(account.getPaypalAccountEmail());

        myCreatorAccountDetails.setAccountType(account.getAccountType());

        myCreatorAccountDetails.setLastLogin(account.getLastLogin());

        myCreatorAccountDetails.setOpenCampaigns(account.getCampaigns().stream()
                .filter(Campaign::isOpen)
                .map(MyCampaignInfoMapper::myCampaignInfoMapper)
                .collect(Collectors.toList()));

        myCreatorAccountDetails.setClosedCampaigns(account.getCampaigns().stream()
                .filter(campaign -> !campaign.isOpen())
                .map(MyCampaignInfoMapper::myCampaignInfoMapper)
                .collect(Collectors.toList()));

        myCreatorAccountDetails.setOutgoingDonations(account.getOutgoingDonations().stream()
                .map(DonationListItemMapper::donationListItemMapper)
                .collect(Collectors.toList()));

        myCreatorAccountDetails.setIncomingDonations(account.getIncomingDonations().stream()
                .map(DonationListItemMapper::donationListItemMapper)
                .collect(Collectors.toList()));

        myCreatorAccountDetails.setFollowers(account.getFollowers().stream()
                .map(FollowAccountDetailMapper::followAccountDetailMapper)
                .collect(Collectors.toList()));

        myCreatorAccountDetails.setFollowings(account.getFollowings().stream()
                .map(FollowAccountDetailMapper::followAccountDetailMapper)
                .collect(Collectors.toList()));

        return myCreatorAccountDetails;

    }

}