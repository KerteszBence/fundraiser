package org.fundraiser.dto.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDonationCommand {

    @Min(message = "The target minimum must be a positive number", value = 1)
    private Double balance;

    @NotNull(message = "Cannot be null!")
    @NotEmpty(message = "Cannot be empty!")
    @NotBlank(message = "Cannot be blank!")
    private String sourceUsername;

    @NotNull(message = "Cannot be null!")
    @NotEmpty(message = "Cannot be empty!")
    @NotBlank(message = "Cannot be blank!")
    private String targetUsername;

    @Min(message = "The id minimum must be 0", value = 0)
    private Long campaignId;

}
