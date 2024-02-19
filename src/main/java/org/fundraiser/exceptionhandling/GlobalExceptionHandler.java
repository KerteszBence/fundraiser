package org.fundraiser.exceptionhandling;

import lombok.extern.slf4j.Slf4j;
import org.fundraiser.exceptionhandling.account.*;
import org.fundraiser.exceptionhandling.campaign.CampaignNotFindByIdException;
import org.fundraiser.exceptionhandling.campaign.CampaignNotFoundByAccountException;
import org.fundraiser.exceptionhandling.campaign.CampaignNotFoundByCategoryException;
import org.fundraiser.exceptionhandling.cloudinary.CloudinaryDownloadException;
import org.fundraiser.exceptionhandling.cloudinary.FileRegistryNotFoundException;
import org.fundraiser.exceptionhandling.cloudinary.ServerStorageDownloadException;
import org.fundraiser.exceptionhandling.donation.DonationNotFindByIdException;
import org.fundraiser.exceptionhandling.security.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationError>> handleValidationException(MethodArgumentNotValidException exception) {
        List<ValidationError> validationErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        validationErrors.forEach(validationError -> {
            log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        });
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFindByIdException.class)
    public ResponseEntity<List<ValidationError>> handleAccountNotFindByIdException(AccountNotFindByIdException exception) {
        ValidationError validationError = new ValidationError("accountId",
                "Account not found with id: " + exception.getId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFindByUserNameException.class)
    public ResponseEntity<List<ValidationError>> handleAccountNotFindByUserNameException(AccountNotFindByUserNameException exception) {
        ValidationError validationError = new ValidationError("accountUsername",
                "Account not found with username: " + exception.getUserName());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(CampaignNotFindByIdException.class)
    public ResponseEntity<List<ValidationError>> handleCampaignNotFindByIdException(CampaignNotFindByIdException exception) {
        ValidationError validationError = new ValidationError("campaignId",
                "campaign not found with id: " + exception.getId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DonationNotFindByIdException.class)
    public ResponseEntity<List<ValidationError>> handleDonationNotFindByIdException(DonationNotFindByIdException exception) {
        ValidationError validationError = new ValidationError("donationId",
                "donation not found with id: " + exception.getId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedAccountException.class)
    public ResponseEntity<List<ValidationError>> handleUnauthorizedAccountException(UnauthorizedAccountException exception) {
        ValidationError validationError = new ValidationError("accountUsername",
                "Account has no access with username: " + exception.getUsername());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNameAlreadyExistException.class)
    public ResponseEntity<List<ValidationError>> handleUserNameAlreadyExistException(UserNameAlreadyExistException exception) {
        ValidationError validationError = new ValidationError("accountUsername",
                "Account is already exist with username: " + exception.getUsername());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<List<ValidationError>> handleEmailAlreadyExistException(EmailAlreadyExistException exception) {
        ValidationError validationError = new ValidationError("email",
                "email is already exist: " + exception.getEmail());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CampaignNotFoundByCategoryException.class)
    public ResponseEntity<List<ValidationError>> handleCampaignNotFoundByCategoryException(CampaignNotFoundByCategoryException exception) {
        ValidationError validationError = new ValidationError("campaignsByCategory",
                "Campaigns not found with category: " + exception.getCategory());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CreatorsNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleCreatorsNotFoundException(CreatorsNotFoundException exception) {
        ValidationError validationError = new ValidationError("creatorsNotFound",
                "Creators not found!");
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CampaignNotFoundByAccountException.class)
    public ResponseEntity<List<ValidationError>> handleCampaignNotFoundByAccountException(CampaignNotFoundByAccountException exception) {
        ValidationError validationError = new ValidationError("username",
                "campaign not found with username: " + exception.getUsername());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CreatorAccountNotFindByUserNameException.class)
    public ResponseEntity<List<ValidationError>> handleCreatorAccountNotFindByUserNameException(CreatorAccountNotFindByUserNameException exception) {
        ValidationError validationError = new ValidationError("creatorUsername",
                "Creator not found with username: " + exception.getUserName());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CloudinaryDownloadException.class)
    public ResponseEntity<List<ValidationError>> handleCloudinaryDownloadException(CloudinaryDownloadException exception) {
        ValidationError validationError = new ValidationError("id",
                "file not found with id: " + exception.getId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileRegistryNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleFileRegistryNotFoundException(FileRegistryNotFoundException exception) {
        ValidationError validationError = new ValidationError("id",
                "file not found with id: " + exception.getId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerStorageDownloadException.class)
    public ResponseEntity<List<ValidationError>> handleServerStorageDownloadException(ServerStorageDownloadException exception) {
        ValidationError validationError = new ValidationError("id",
                "file not found with id: " + exception.getId());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountIsNotEnabledException.class)
    public ResponseEntity<List<ValidationError>> handleAccountIsNotEnabledException(AccountIsNotEnabledException exception) {
        ValidationError validationError = new ValidationError("username",
                "Can't create a campaign with user: " + exception.getUserName() + " because the account isn't verified...");
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(PasswordsDontMatchException.class)
    public ResponseEntity<List<ValidationError>> handlePasswordsDontMatchException(PasswordsDontMatchException exception) {
        ValidationError validationError = new ValidationError("password",
                "The provided passwords dont match");
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OldPasswordIsNotCorrectException.class)
    public ResponseEntity<List<ValidationError>> handleOldPasswordIsNotCorrectException(OldPasswordIsNotCorrectException exception) {
        ValidationError validationError = new ValidationError("password",
                "The provided old password isn't correct...");
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFindByEmailException.class)
    public ResponseEntity<List<ValidationError>> handleAccountNotFindByEmailException(AccountNotFindByEmailException exception) {
        ValidationError validationError = new ValidationError("email",
                "Account not found with email: " + exception.getEmail());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<List<ValidationError>> handleTokenNotFoundException(TokenNotFoundException exception) {
        ValidationError validationError = new ValidationError("token",
                "Token not found: " + exception.getMessage());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TokenAlreadyConfirmedException.class)
    public ResponseEntity<List<ValidationError>> handleTokenAlreadyConfirmedException(TokenAlreadyConfirmedException exception) {
        ValidationError validationError = new ValidationError("token",
                "Token already confirmed: " + exception.getMessage());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<List<ValidationError>> handleTokenExpiredException(TokenExpiredException exception) {
        ValidationError validationError = new ValidationError("token",
                "Token expired: " + exception.getMessage());
        log.error("Error in validation: " + validationError.getField() + ": " + validationError.getErrorMessage());
        return new ResponseEntity<>(List.of(validationError), HttpStatus.BAD_REQUEST);
    }

}
