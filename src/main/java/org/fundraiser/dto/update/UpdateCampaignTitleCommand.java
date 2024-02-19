package org.fundraiser.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCampaignTitleCommand {

    @NotNull(message = "Cannot be null!")
    @Size(message = "The title must be between 4 and 32 characters", min = 4, max = 32)
    @NotEmpty(message = "Cannot be empty!")
    @NotBlank(message = "Cannot be blank!")
    private String title;

}
