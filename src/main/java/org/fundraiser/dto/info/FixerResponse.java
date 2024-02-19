package org.fundraiser.dto.info;

import lombok.Data;

import java.util.Map;

@Data
public class FixerResponse {

    private boolean success;

    private long timestamp;

    private String base;

    private Map<String, Double> rates;

}
