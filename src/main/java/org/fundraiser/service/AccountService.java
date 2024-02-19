package org.fundraiser.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.fundraiser.config.AccountType;
import org.fundraiser.config.security.SecretConfig;
import org.fundraiser.domain.Account;
import org.fundraiser.domain.ConfirmationToken;
import org.fundraiser.dto.EmailData;
import org.fundraiser.dto.create.CreateCreatorCommand;
import org.fundraiser.dto.create.CreateDonorCommand;
import org.fundraiser.dto.info.*;
import org.fundraiser.dto.security.*;
import org.fundraiser.dto.update.*;
import org.fundraiser.event.AccountDeletionEvent;
import org.fundraiser.exceptionhandling.account.*;
import org.fundraiser.exceptionhandling.security.OldPasswordIsNotCorrectException;
import org.fundraiser.exceptionhandling.security.PasswordsDontMatchException;
import org.fundraiser.repository.AccountRepository;
import org.fundraiser.utils.AccountUtils;
import org.fundraiser.utils.EmailTemplates;
import org.fundraiser.utils.mapper.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSenderService emailSenderService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AccountService(AccountRepository accountRepository, ModelMapper modelMapper, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, ConfirmationTokenService confirmationTokenService, EmailSenderService emailSenderService, ApplicationEventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
        this.emailSenderService = emailSenderService;
        this.eventPublisher = eventPublisher;
    }

    public Account findAccountById(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFindByIdException(id);
        }
        return accountOptional.get();
    }

    public Account findAccountByUserName(String username) {
        Optional<Account> accountOptional = accountRepository.findAccountByUserName(username);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFindByUserNameException(username);
        }
        return accountOptional.get();
    }

    public Account findAccountByEmail(String email) {
        Optional<Account> accountOptional = accountRepository.findAccountByEmail(email);
        if (accountOptional.isEmpty()) {
            throw new AccountNotFindByEmailException(email);
        }
        return accountOptional.get();
    }

    public MyCreatorAccountDetails getMyCreatorAccountByUserName(String userName) { // validálni!!! frontenden???
        return MyCreatorAccountDetailsMapper.creatorAccountDetailsMapper(findAccountByUserName(userName));
    }

    public MyDonorAccountDetails getMyDonorAccountByUserName(String userName) { // validálni!!! frontenden???
        return MyDonorAccountDetailsMapper.donorAccountDetailsMapper(findAccountByUserName(userName));
    }

    public CreatorAccountInfo getCreatorAccountInfo(String userName) {
        return CreatorAccountInfoMapper.creatorAccountInfoMapper(findAccountByUserName(userName));
    }

    public DonorAccountInfo getDonorAccountInfoByUserName(String userName) {
        return DonorAccountInfoMapper.donorAccountInfoMapper(findAccountByUserName(userName));
    }

    public void saveCreatorAccount(CreateCreatorCommand command) {
        if (accountRepository.userNameIsExist(command.getUsername())) {
            throw new UserNameAlreadyExistException(command.getUsername());
        } else if (!command.getPassword().equals(command.getPasswordAgain())) {
            throw new PasswordsDontMatchException();
        }
        Account account = modelMapper.map(command, Account.class);
        account.setPassword(passwordEncoder.encode(command.getPassword()));
        account.setAccountType(AccountType.ROLE_CREATOR);
        account.setProfilePictureId(AccountUtils.ACCOUNT_BASIC_PROFILE_PICTURE_URL);
        account.setCoverPictureId(AccountUtils.ACCOUNT_BASIC_COVER_PICTURE_URL);
        account.setIsEnabled(false);
        account.setPaypalAccountEmail(command.getPaypalEmail());
        accountRepository.save(account);

        sendNewConfirmationToken(account.getUsername());
    }

    public void saveDonorAccount(CreateDonorCommand command) {
        if (accountRepository.userNameIsExist(command.getUsername())) {
            throw new UserNameAlreadyExistException(command.getUsername());
        } else if (!command.getPassword().equals(command.getPasswordAgain())) {
            throw new PasswordsDontMatchException();
        }
        Account account = modelMapper.map(command, Account.class);
        account.setPassword(passwordEncoder.encode(command.getPassword()));
        account.setAccountType(AccountType.ROLE_DONOR);
        account.setProfilePictureId(AccountUtils.ACCOUNT_BASIC_PROFILE_PICTURE_URL);
        account.setCoverPictureId(AccountUtils.ACCOUNT_BASIC_COVER_PICTURE_URL);
        account.setIsEnabled(false);
        accountRepository.save(account);

        sendNewConfirmationToken(account.getUsername());
    }
    public void updatePassword(String username, UpdatePasswordCommand command) {
        Account account = findAccountByUserName(username);
        if (!account.getPassword().equals(passwordEncoder.encode(command.getOldPassword()))) {
            throw new OldPasswordIsNotCorrectException();
        } else if (!command.getPassword().equals(command.getPasswordAgain())) {
            throw new PasswordsDontMatchException();
        }
        command.setPassword(passwordEncoder.encode(command.getPassword()));
        modelMapper.map(command, account);
    }
    public void confirmRegistrationToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.confirmToken(token);
        Account account = confirmationToken.getAccount();
        account.setIsEnabled(true);

        EmailData emailData = new EmailData(account.getEmail(),
                EmailTemplates.SUCCESSFUL_REGISTRATION_SUBJECT,
                EmailTemplates.successfulRegistrationMessage(account.getFullName()));

        emailSenderService.sendEmail(emailData);
    }

    public void sendNewConfirmationToken(String username) {
        Account account = findAccountByUserName(username);
        ConfirmationToken confirmationToken = confirmationTokenService.createConfirmationToken(account);

        EmailData emailData = new EmailData(account.getEmail(),
                EmailTemplates.VERIFICATION_SUBJECT,
                EmailTemplates.verificationMessage(account.getFullName(), confirmationToken.getToken()));

        emailSenderService.sendEmail(emailData);
    }

    public void updatePaypalEmail(String username, UpdatePaypalEmail command) {
        Account account = findAccountByUserName(username);
        modelMapper.map(command, account);
    }

    public void updateProfilePictureId(String username, UpdateProfilePictureIdCommand command) {
        Account account = findAccountByUserName(username);
        modelMapper.map(command, account);
    }

    public void updateCoverPictureId(String username, UpdateCoverPictureIdCommand command) {
        Account account = findAccountByUserName(username);
        modelMapper.map(command, account);
    }

    public void updateEmail(String username, UpdateEmailCommand command) {
        Account account = findAccountByUserName(username);
        if (accountRepository.emailIsExist(command.getEmail())) {
            throw new EmailAlreadyExistException(command.getEmail());
        }
        modelMapper.map(command, account);
    }

    public void forgotPassword(ForgotPasswordCommand command) {

        Account account = !command.getUsernameOrEmail().contains("@") ?
                findAccountByUserName(command.getUsernameOrEmail()) :
                findAccountByEmail(command.getUsernameOrEmail());

        ConfirmationToken confirmationToken = confirmationTokenService.createConfirmationToken(account);

        EmailData emailData = new EmailData(account.getEmail(),
                EmailTemplates.RESET_PASSWORD_SUBJECT,
                EmailTemplates.resetPasswordMessage(account.getFullName(), confirmationToken.getToken()));
        emailSenderService.sendEmail(emailData);
    }

    public void resetPassword(ResetPasswordCommand command) {
        if (!command.getPassword().equals(command.getPasswordAgain())) {
            throw new PasswordsDontMatchException();
        }
        Account account = confirmationTokenService.confirmToken(command.getToken()).getAccount();

        account.setPassword(passwordEncoder.encode(command.getPassword()));
        accountRepository.save(account);
    }

    public void changeAccountTypeDonorToCreator(String username, UpdatePaypalEmail command) { // frontenden feltétlen be kell kérni a paypalEmail-t váltáskor (Put kérés lesz!!!)
        Account account = findAccountByUserName(username);
        account.setAccountType(AccountType.ROLE_CREATOR);
        account.setPaypalAccountEmail(command.toString());
        accountRepository.save(account);
    }

    public AccountResponse getAllCreators(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Account> accounts = accountRepository.findCreatorAccount(pageable);
        List<Account> accountList = accounts.getContent();
        if (accountList.isEmpty()) {
            throw new CreatorsNotFoundException();
        }
        List<CreatorAccountInfo> content = accountList.stream().map(CreatorAccountInfoMapper::creatorAccountInfoMapper).collect(Collectors.toList());

        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setContent(content);
        accountResponse.setPageNo(accounts.getNumber());
        accountResponse.setPageSize(accounts.getSize());
        accountResponse.setTotalElements(accounts.getTotalElements());
        accountResponse.setTotalPages(accounts.getTotalPages());
        accountResponse.setLast(accounts.isLast());

        return accountResponse;
    }

    public void setFollowing(String source, String target) {
        Account sourceAccount = findAccountByUserName(source);
        Account targetAccount = findAccountByUserName(target);
        if (targetAccount.getAccountType().equals(AccountType.ROLE_CREATOR)) {
            sourceAccount.getFollowings().add(targetAccount);
            targetAccount.getFollowers().add(sourceAccount);
        } else {
            throw new CreatorAccountNotFindByUserNameException(target);
        }
    }

    public List<FollowAccountDetail> getFollowersList(String username) {
        Account account = findAccountByUserName(username);
        return account.getFollowers()
                .stream()
                .map(follower -> modelMapper.map(follower, FollowAccountDetail.class))
                .collect(Collectors.toList());
    }

    public List<FollowAccountDetail> getFollowingsList(String username) {
        Account account = findAccountByUserName(username);
        return account.getFollowings()
                .stream()
                .map(following -> modelMapper.map(following, FollowAccountDetail.class))
                .collect(Collectors.toList());
    }

    public List<DonationListItem> getMyAllOutgoingDonations(String username) { // lekérni a kimenő donation-okat, a creatorok a beérkezőket a campaign-ok alatt tudja lekérni
        Account account = findAccountByUserName(username);
        return account.getOutgoingDonations().stream()
                .map(DonationListItemMapper::donationListItemMapper)
                .collect(Collectors.toList());
    }

    public void deleteFollowing(Account account) {
        List<Account> followingAccount = account.getFollowings();
        for (Account account1 : followingAccount) {
            account1.getFollowers().remove(account);
        }
    }

    public void deleteFollowers(Account account) {
        List<Account> followersAccount = account.getFollowers();
        for (Account account1 : followersAccount) {
            account1.getFollowings().remove(account);
        }
    }

    public void deleteAccount(String username) {
        Account account = findAccountByUserName(username);
        log.info("sending AccountDeletionEvent with account: " + account.getUsername());
        eventPublisher.publishEvent(new AccountDeletionEvent(this, account));
        log.info("AccountDeletionEvent complete with account: " + account.getUsername());
        deleteFollowing(account);
        deleteFollowers(account);
        confirmationTokenService.deleteConfirmationToken(account);
        accountRepository.delete(account);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account user = findAccountByUserName(username);

        String[] roles = new String[1];
        roles[0] = user.getAccountType().toString();

        return User
                .withUsername(user.getUsername())
                .authorities(AuthorityUtils.createAuthorityList(roles))
                .password(user.getPassword())
                .build();
    }

    public AuthenticationResponseDto login(AuthenticationRequestDto requestDto) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        User user = (User) authentication.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256(SecretConfig.getSecret().getBytes());
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
//                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withExpiresAt(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000))
                .withIssuer("http://ec2-3-123-128-129.eu-central-1.compute.amazonaws.com:8080/api/accounts/login")
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000))
                .withIssuer("http://ec2-3-123-128-129.eu-central-1.compute.amazonaws.com:8080/api/accounts/login")
                .sign(algorithm);

        Account account = findAccountByUserName(requestDto.getUsername());
        account.setLastLogin(LocalDateTime.now());
        Long userID = account.getId();

        return new AuthenticationResponseDto(accessToken, refreshToken, new AccountDetails(userID, user));
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        String refreshToken = refreshTokenRequest.getRefreshToken();
        Algorithm algorithm = Algorithm.HMAC256(SecretConfig.getSecret().getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);
        String username = decodedJWT.getSubject();

        Account user = findAccountByUserName(username);
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer("http://ec2-3-123-128-129.eu-central-1.compute.amazonaws.com:8080/api/accounts/refreshtoken")
                .withClaim("roles", List.of(user.getAccountType().toString()))
                .sign(algorithm);

        Account account = findAccountByUserName(username);
        account.setLastLogin(LocalDateTime.now());

        return new RefreshTokenResponse(accessToken);
    }
}