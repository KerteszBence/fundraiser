package org.fundraiser.utils.mapper;

import org.fundraiser.domain.Donation;
import org.fundraiser.dto.info.DonationListItem;

public class DonationListItemMapper {

    public static DonationListItem donationListItemMapper(Donation donation) {

        DonationListItem donationListItem = new DonationListItem();

        donationListItem.setSourceName(donation.getSource().getUsername());

        donationListItem.setTargetName(donation.getTarget().getUsername());

        donationListItem.setCampaignTitle(donation.getCampaign().getTitle());

        donationListItem.setAmount(donation.getBalance());

        donationListItem.setTimestamp(donation.getTransactionTime().toString());

        return donationListItem;

    }

}
