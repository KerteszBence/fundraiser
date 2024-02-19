package org.fundraiser.dto.info;

import lombok.Data;

import java.util.List;

@Data
public class CampaignResponse {

    private List<CampaignInfo> content;

    private int pageNo;

    private int pageSize;

    private long totalElements;

    private int totalPages;

    private boolean last;

}