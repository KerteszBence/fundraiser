package org.fundraiser.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePaypalEmail {

    @NotNull(message = "Cannot be null!")
    @NotEmpty(message = "Cannot be empty!")
    @NotBlank(message = "Cannot be blank!")
    @Email(message = "Please enter a valid email!", regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String paypalEmail;

}
