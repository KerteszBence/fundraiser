package org.fundraiser.utils.mapper;

import org.fundraiser.domain.Account;
import org.fundraiser.dto.info.FollowAccountDetail;

public class FollowAccountDetailMapper {

    public static FollowAccountDetail followAccountDetailMapper(Account account) {

        FollowAccountDetail followAccountDetail = new FollowAccountDetail();

        followAccountDetail.setUsername(account.getUsername());

        return followAccountDetail;

    }

}
