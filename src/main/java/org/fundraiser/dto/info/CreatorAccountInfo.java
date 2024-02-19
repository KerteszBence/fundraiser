package org.fundraiser.dto.info;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.fundraiser.config.AccountType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class CreatorAccountInfo { // ha valamelyik account lekéri egy másik account adatait!

    private String username;

    private AccountType accountType;

    private LocalDateTime lastLogin;

    private List<CampaignInfo> campaignInfoList;

}
