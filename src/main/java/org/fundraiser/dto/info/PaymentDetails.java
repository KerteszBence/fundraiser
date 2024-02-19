package org.fundraiser.dto.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentDetails {

    private String total;

    private String currency;

    private String method;

    private String intent;

    private String description;

    private String campaignId;

    private String cancelUrl;

    private String successUrl;

}
