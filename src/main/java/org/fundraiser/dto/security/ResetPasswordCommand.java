package org.fundraiser.dto.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordCommand {


    @NotNull(message = "Cannot be null!")
    @NotEmpty(message = "Cannot be empty!")
    @NotBlank(message = "Cannot be blank!")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()-_+={}:|;]).{8,}$", message = "The password must be at least 8 characters long and must contain: - at least 1 special character (~`!@#$%^&*()-_+={}:|;)\n - 1 number\n - 1 lower case letter\n - 1 upper case letter") //added extra accepted special characters
    private String password;

    @NotNull(message = "Cannot be null!")
    @NotEmpty(message = "Cannot be empty!")
    @NotBlank(message = "Cannot be blank!")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()-_+={}:|;]).{8,}$", message = "The password must be at least 8 characters long and must contain: - at least 1 special character (~`!@#$%^&*()-_+={}:|;)\n - 1 number\n - 1 lower case letter\n - 1 upper case letter") //added extra accepted special characters
    private String passwordAgain;

    @NotNull(message = "Cannot be null!")
    @NotEmpty(message = "Cannot be empty!")
    @NotBlank(message = "Cannot be blank!")
    private String token;

}