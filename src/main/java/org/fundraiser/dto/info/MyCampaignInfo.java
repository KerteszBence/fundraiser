package org.fundraiser.dto.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyCampaignInfo {

    private Long id;

    private String accountName;

    private String category;

    private String title;

    private String introduction;

    private String projectDescription;

    private Long avatarId;

    private Long coverId;

    private Long introVideoId;

    private Double target;

    private Double collected;

    private boolean isOpen;

    private LocalDateTime created;

    private LocalDateTime expirationDate;

    private Long likeCounter;

    private String currency;

    List<DonationListItem> donations;

    private LocalDateTime lastModification;

}
