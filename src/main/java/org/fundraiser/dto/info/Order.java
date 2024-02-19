package org.fundraiser.dto.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {

    private Double price;

    private String currency;

    private String method;

    private String intent;

    private String description;

    private String payer;

    private Long campaignId;

}
