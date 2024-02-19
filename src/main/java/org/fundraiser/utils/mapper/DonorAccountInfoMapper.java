package org.fundraiser.utils.mapper;

import org.fundraiser.domain.Account;
import org.fundraiser.dto.info.DonorAccountInfo;

public class DonorAccountInfoMapper {

    public static DonorAccountInfo donorAccountInfoMapper(Account account) {

        DonorAccountInfo donorAccountInfo = new DonorAccountInfo();

        donorAccountInfo.setUsername(account.getUsername());

        donorAccountInfo.setAccountType(account.getAccountType());

        donorAccountInfo.setLastLogin(account.getLastLogin());

        return donorAccountInfo;

    }

}
