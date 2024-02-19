package org.fundraiser.utils.mapper;

import org.fundraiser.domain.Campaign;
import org.fundraiser.dto.info.MyCampaignInfo;

import java.util.stream.Collectors;

public class MyCampaignInfoMapper {

    public static MyCampaignInfo myCampaignInfoMapper(Campaign campaign) {

        MyCampaignInfo myCampaignInfo = new MyCampaignInfo();

        myCampaignInfo.setId(campaign.getId());

        myCampaignInfo.setCategory(campaign.getCategory().toString());

        myCampaignInfo.setTitle(campaign.getTitle());

        myCampaignInfo.setIntroduction(campaign.getIntroduction());

        myCampaignInfo.setProjectDescription(campaign.getProjectDescription());

        myCampaignInfo.setAvatarId(campaign.getAvatarId());

        myCampaignInfo.setCoverId(campaign.getCoverId());

        myCampaignInfo.setIntroVideoId(campaign.getIntroVideoId());

        myCampaignInfo.setTarget(campaign.getTarget());

        myCampaignInfo.setCollected(campaign.getCollected());

        myCampaignInfo.setOpen(campaign.isOpen());

        myCampaignInfo.setCreated(campaign.getCreated());

        myCampaignInfo.setExpirationDate(campaign.getExpirationDate());

        myCampaignInfo.setLikeCounter(campaign.getLikeCounter());

        myCampaignInfo.setAccountName(campaign.getAccount().getUsername());

        myCampaignInfo.setCurrency(campaign.getCurrency());

        myCampaignInfo.setDonations(campaign.getDonations().stream()
                .map(DonationListItemMapper::donationListItemMapper)
                .collect(Collectors.toList()));

        myCampaignInfo.setLastModification(campaign.getLastModification());

        return myCampaignInfo;

    }

}
