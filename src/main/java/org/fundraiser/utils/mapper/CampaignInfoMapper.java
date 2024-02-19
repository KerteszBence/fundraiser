package org.fundraiser.utils.mapper;

import org.fundraiser.domain.Campaign;
import org.fundraiser.dto.info.CampaignInfo;

import java.util.stream.Collectors;

public class CampaignInfoMapper {

    public static CampaignInfo campaignInfoMapper(Campaign campaign) {

        CampaignInfo campaignInfo = new CampaignInfo();

        campaignInfo.setId(campaign.getId());

        campaignInfo.setCategory(campaign.getCategory().toString());

        campaignInfo.setTitle(campaign.getTitle());

        campaignInfo.setIntroduction(campaign.getIntroduction());

        campaignInfo.setProjectDescription(campaign.getProjectDescription());

        campaignInfo.setAvatarId(campaign.getAvatarId());

        campaignInfo.setCoverId(campaign.getCoverId());

        campaignInfo.setIntroVideoId(campaign.getIntroVideoId());

        campaignInfo.setTarget(campaign.getTarget());

        campaignInfo.setCollected(campaign.getCollected());

        campaignInfo.setOpen(campaign.isOpen());

        campaignInfo.setCreated(campaign.getCreated());

        campaignInfo.setExpirationDate(campaign.getExpirationDate());

        campaignInfo.setLikeCounter(campaign.getLikeCounter());

        campaignInfo.setAccountId(campaign.getAccount().getId());

        campaignInfo.setAccountName(campaign.getAccount().getUsername());

        campaignInfo.setCurrency(campaign.getCurrency());

        campaignInfo.setDonations(campaign.getDonations().stream()
                .map(DonationListItemMapper::donationListItemMapper)
                .collect(Collectors.toList()));

        campaignInfo.setLastModification(campaign.getLastModification());

        return campaignInfo;

    }

}
