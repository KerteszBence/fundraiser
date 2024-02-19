package org.fundraiser.dto.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDonorCommand {

    @NotNull(message = "Cannot be null!")
    @Size(message = "The username must be between 4 and 32 characters", min = 4, max = 32)
    @NotEmpty(message = "Cannot be empty!")
    @NotBlank(message = "Cannot be blank!")
    @Pattern(regexp = "^[^@]+$", message = "The username can't contain a '@' character.")
    private String username;

    @NotNull(message = "Cannot be null!")
    @Email(message = "Please enter a valid email!", regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotEmpty(message = "Cannot be empty!")
    @NotBlank(message = "Cannot be blank!")
    private String email;

    @NotNull(message = "Cannot be null!")
    @NotEmpty(message = "Cannot be empty!")
    @NotBlank(message = "Cannot be blank!")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()-_+={}:|;]).{8,}$", message = "The password must be at least 8 characters long and must contain: - at least 1 special character (~`!@#$%^&*()-_+={}:|;)\n - 1 number\n - 1 lower case letter\n - 1 upper case letter")
    private String password;

    @NotNull(message = "Cannot be null!")
    @NotEmpty(message = "Cannot be empty!")
    @NotBlank(message = "Cannot be blank!")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()-_+={}:|;]).{8,}$", message = "The password must be at least 8 characters long and must contain: - at least 1 special character (~`!@#$%^&*()-_+={}:|;)\n - 1 number\n - 1 lower case letter\n - 1 upper case letter")
    private String passwordAgain;

}