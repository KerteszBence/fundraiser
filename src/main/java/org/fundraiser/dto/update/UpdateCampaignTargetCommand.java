package org.fundraiser.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCampaignTargetCommand {

    @Min(message = "The target minimum must be a positive number", value = 1)
    private Double target;

}
