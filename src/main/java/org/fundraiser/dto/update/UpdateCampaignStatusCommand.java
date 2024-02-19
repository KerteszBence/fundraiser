package org.fundraiser.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCampaignStatusCommand {

    @NotNull(message = "Cannot be null!")
    private Boolean isOpen;

}