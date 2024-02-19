package org.fundraiser.contoller;

import lombok.extern.slf4j.Slf4j;
import org.fundraiser.dto.create.CreateCreatorCommand;
import org.fundraiser.dto.create.CreateDonorCommand;
import org.fundraiser.dto.info.*;
import org.fundraiser.dto.security.*;
import org.fundraiser.dto.update.*;
import org.fundraiser.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/creator")
    public ResponseEntity<Void> registerCreatorAccount(@Valid @RequestBody CreateCreatorCommand command) {
        log.info(("Http request, POST, / /api/accounts/creator: " + command.toString()));
        accountService.saveCreatorAccount(command);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/donor")
    public ResponseEntity<Void> registerDonorAccount(@Valid @RequestBody CreateDonorCommand command) {
        log.info(("Http request, POST, / /api/accounts/donor: " + command.toString()));
        accountService.saveDonorAccount(command);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/my_creator/{username}")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<MyCreatorAccountDetails> getMyCreatorAccountInfoByUserName(@PathVariable("username") String username) {
        log.info("Http request, GET, / /api/accounts/creator/{username} with variable: " + username);
        return new ResponseEntity<>(accountService.getMyCreatorAccountByUserName(username), HttpStatus.OK);
    }

    @GetMapping("/my_donor/{username}")
    @Secured({"ROLE_DONOR", "ROLE_ADMIN"})
    public ResponseEntity<MyDonorAccountDetails> getMyDonorAccountInfoByUserName(@PathVariable("username") String username) {
        log.info("Http request, GET, / /api/accounts/donor/{username} with variable: " + username);
        return new ResponseEntity<>(accountService.getMyDonorAccountByUserName(username), HttpStatus.OK);
    }

    @GetMapping("/creator/{username}")
    public ResponseEntity<CreatorAccountInfo> getCreatorAccountInfoByUserName(@PathVariable("username") String username) {
        log.info("Http request, GET, / /api/accounts/creator_account/{username} with variable: " + username);
        return new ResponseEntity<>(accountService.getCreatorAccountInfo(username), HttpStatus.OK);
    }

    @GetMapping("/donor/{username}")
    public ResponseEntity<DonorAccountInfo> getDonorAccountInfoByUserName(@PathVariable("username") String username) {
        log.info("Http request, GET, / /api/accounts/donor_account/{username} with variable: " + username);
        return new ResponseEntity<>(accountService.getDonorAccountInfoByUserName(username), HttpStatus.OK);
    }

    @GetMapping("/all_creator_accounts")
    public ResponseEntity<AccountResponse> getAllCreatorsAccount(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
                                                                 @RequestParam(value = "pageSize", defaultValue = "12", required = false) int pageSize) {
        log.info("Http request, GET, / /api/accounts/all_creator_accounts");
        return new ResponseEntity<>(accountService.getAllCreators(pageNo, pageSize), HttpStatus.OK);
    }

    @GetMapping("/my_donations/{username}")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<List<DonationListItem>> getMyAllOutgoingDonations(@PathVariable("username") String username) {
        log.info("Http request, GET, / /api/accounts//donations/{username} with variable: " + username);
        return new ResponseEntity<>(accountService.getMyAllOutgoingDonations(username), HttpStatus.OK);
    }

    @PutMapping("/{username}/paypal")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN"})
    public ResponseEntity<Void> updatePaypalEmail(@PathVariable("username") String username, @Valid @RequestBody UpdatePaypalEmail command) {
        log.info("Http request, PUT, / /api/accounts/{username}/paypal with variable: " + username + "with body: " + command.toString());
        accountService.updatePaypalEmail(username, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{username}/profilepicture")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<Void> updateProfilePictureId(@PathVariable("username") String username, @Valid @RequestBody UpdateProfilePictureIdCommand command) {
        log.info("Http request, PUT, / /api/accounts/{username}/profilepicture with variable: " + username + "with body: " + command.toString());
        accountService.updateProfilePictureId(username, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{username}/coverpicture")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<Void> updateCoverPictureId(@PathVariable("username") String username, @Valid @RequestBody UpdateCoverPictureIdCommand command) {
        log.info("Http request, PUT, / /api/accounts/{username}/coverpicture with variable: " + username + "with body: " + command.toString());
        accountService.updateCoverPictureId(username, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{username}/email")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<Void> updateEmail(@PathVariable("username") String username, @Valid @RequestBody UpdateEmailCommand command) {
        log.info("Http request, PUT, / /api/accounts/{username}/email with variable: " + username + "with body: " + command.toString());
        accountService.updateEmail(username, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{username}/password")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<Void> updatePassword(@PathVariable("username") String username, @Valid @RequestBody UpdatePasswordCommand command) {
        log.info("Http request, PUT, / /api/accounts/{username}/pasword with variable: " + username + "with body: " + command.toString());
        accountService.updatePassword(username, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordCommand command) {
        log.info("Http request, POST, / /api/accounts/forgot_password: " + command.toString());
        accountService.forgotPassword(command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/reset_password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordCommand command) {
        log.info("Http request, PUT, / /api/accounts/reset_password: " + command.toString());
        accountService.resetPassword(command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{username}/upgrade_to_creator")
    @Secured({"ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<Void> changeAccountTypeDonorToCreator(@PathVariable("username") String username, @Valid @RequestBody UpdatePaypalEmail command) {
        log.info("Http request, PUT, / /api/accounts/{username}/change_account_type with variable: " + username + "with body: " + command.toString());
        accountService.changeAccountTypeDonorToCreator(username, command);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{source}/setfollowing/{target}")
    @Secured({"ROLE_ADMIN", "ROLE_DONOR", "ROLE_CREATOR"})
    public ResponseEntity<Void> setFollowing(@PathVariable("source") String source, @PathVariable("target") String target) {
        log.info("Http request, PUT, / /api/accounts/{source}/setfollowing/{target} with source: " + source + " and with target: " + target);
        accountService.setFollowing(source, target);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<List<FollowAccountDetail>> getFollowersList(@PathVariable("username") String username) {
        log.info("Http request, GET, / /api/accounts/{username}/followers with variable: " + username);
        return new ResponseEntity<>(accountService.getFollowersList(username), HttpStatus.OK);
    }

    @GetMapping("/{username}/followings")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<List<FollowAccountDetail>> getFollowingsList(@PathVariable("username") String username) {
        log.info("Http request, GET, / /api/accounts/{username}/followings with variable: " + username);
        return new ResponseEntity<>(accountService.getFollowingsList(username), HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    @Secured({"ROLE_CREATOR", "ROLE_ADMIN", "ROLE_DONOR"})
    public ResponseEntity<Void> deleteAccount(@PathVariable("username") String username) {
        log.info("Http request, DELETE, / /api/accounts/{username} with variable: " + username);
        accountService.deleteAccount(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody AuthenticationRequestDto requestDto) {
        log.info(("Http request, POST, / /api/accounts/login: " + requestDto.toString()));
        AuthenticationResponseDto responseDto = accountService.login(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshTokenResponse tokenResponse = accountService.refreshToken(refreshTokenRequest);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }

    @GetMapping(path = "/verify_account")
    public ResponseEntity<Void> verifyAccount(@RequestParam("token") String token) {
        log.info("Http request, GET, / /api/accounts/verify with variable: " + token);
        accountService.confirmRegistrationToken(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/newconfirmationtoken/{username}")
    @Secured({"ROLE_CREATOR", "ROLE_DONOR", "ROLE_ADMIN"})
    public ResponseEntity<Void> sendNewConfirmationToken(@PathVariable("username") String username) {
        log.info("Http request, GET, / /api/accounts/newconfirmationtoken/{username} with variable: " + username);
        accountService.sendNewConfirmationToken(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
