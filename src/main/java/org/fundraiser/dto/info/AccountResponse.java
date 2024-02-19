package org.fundraiser.dto.info;

import lombok.Data;

import java.util.List;

@Data
public class AccountResponse {

    private List<CreatorAccountInfo> content;

    private int pageNo;

    private int pageSize;

    private long totalElements;

    private int totalPages;

    private boolean last;

}