package org.fundraiser.utils.mapper;

import org.fundraiser.domain.Account;
import org.fundraiser.domain.Campaign;
import org.fundraiser.dto.info.CreatorAccountInfo;

import java.util.stream.Collectors;

public class CreatorAccountInfoMapper {

    public static CreatorAccountInfo creatorAccountInfoMapper(Account account) {

        CreatorAccountInfo creatorAccountInfo = new CreatorAccountInfo();

        creatorAccountInfo.setUsername(account.getUsername());

        creatorAccountInfo.setAccountType(account.getAccountType());

        creatorAccountInfo.setLastLogin(account.getLastLogin());

        creatorAccountInfo.setCampaignInfoList(account.getCampaigns().stream()
                .filter(Campaign::isOpen)
                .map(CampaignInfoMapper::campaignInfoMapper)
                .collect(Collectors.toList()));

        return creatorAccountInfo;
    }

}
