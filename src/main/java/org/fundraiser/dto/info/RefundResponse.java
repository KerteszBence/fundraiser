package org.fundraiser.dto.info;

import lombok.Data;

@Data
public class RefundResponse {

    private String refundId;

    private String currency;

    private String amount;

    private String status;

}