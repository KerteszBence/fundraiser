package org.fundraiser.dto.info;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DonationListItem {

    private String sourceName;

    private String targetName;

    private String campaignTitle;

    private Double amount;

    private String timestamp;

}