package org.fundraiser.utils;

public class EmailTemplates {
    public static final String VERIFICATION_SUBJECT = "Account verification";
    public static final String RESET_PASSWORD_SUBJECT = "Password reset";
    public static final String SUCCESSFUL_REGISTRATION_SUBJECT = "Registration Successful";


    public static String verificationMessage(String name, String token) {
        return String.format("Dear %s,\n\nThank you for registering on Fundiverse! "
                + "To validate your account, please click on the following link:\n\nhttp://ec2-3-123-128-129.eu-central-1.compute.amazonaws.com:8080/api/accounts/verify_account?token=%s", name, token);
    }

    public static String resetPasswordMessage(String name, String token) {
        return String.format("Dear %s,\n\nYou have requested a password reset on Fundiverse. "
                + "To reset your password, please click on the following link:\n\nhttp://ec2-3-123-128-129.eu-central-1.compute.amazonaws.com:8080/api/accounts/reset_password?token=%s", name, token);
    }

    public static String successfulRegistrationMessage(String name) {
        return String.format("Dear %s,\n\nCongratulations! Your registration on Fundiverse was successful."
                + "\nYou can now log in and explore our platform, please click on the following link:\n\nhttp://ec2-3-123-128-129.eu-central-1.compute.amazonaws.com:8080/api/accounts/login", name);
    }

}
