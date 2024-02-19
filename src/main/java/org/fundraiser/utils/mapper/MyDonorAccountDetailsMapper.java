package org.fundraiser.utils.mapper;

import org.fundraiser.domain.Account;
import org.fundraiser.dto.info.MyDonorAccountDetails;

import java.util.stream.Collectors;

public class MyDonorAccountDetailsMapper {

    public static MyDonorAccountDetails donorAccountDetailsMapper(Account account) {

        MyDonorAccountDetails myDonorAccountDetails = new MyDonorAccountDetails();

        myDonorAccountDetails.setUsername(account.getUsername());

        myDonorAccountDetails.setEmail(account.getEmail());

        myDonorAccountDetails.setPaypalEmail(account.getPaypalAccountEmail());

        myDonorAccountDetails.setAccountType(account.getAccountType());

        myDonorAccountDetails.setLastLogin(account.getLastLogin());

        myDonorAccountDetails.setOutgoingDonations(account.getOutgoingDonations().stream()
                .map(DonationListItemMapper::donationListItemMapper)
                .collect(Collectors.toList()));

        myDonorAccountDetails.setFollowings(account.getFollowings().stream()
                .map(FollowAccountDetailMapper::followAccountDetailMapper)
                .collect(Collectors.toList()));

        return myDonorAccountDetails;

    }

}