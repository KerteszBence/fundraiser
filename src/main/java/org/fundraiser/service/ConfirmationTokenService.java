package org.fundraiser.service;

import org.fundraiser.domain.Account;
import org.fundraiser.domain.ConfirmationToken;
import org.fundraiser.exceptionhandling.security.TokenAlreadyConfirmedException;
import org.fundraiser.exceptionhandling.security.TokenExpiredException;
import org.fundraiser.exceptionhandling.security.TokenNotFoundException;
import org.fundraiser.repository.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public ConfirmationToken createConfirmationToken(Account account) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);
        saveConfirmationToken(confirmationToken);
        return confirmationToken;
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public ConfirmationToken confirmToken(String token) {
        ConfirmationToken confirmationToken = getToken(token)
                .orElseThrow(() -> new TokenNotFoundException());

        if (confirmationToken.getConfirmedAt() != null) {
            throw new TokenAlreadyConfirmedException();
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }

        confirmationTokenRepository.updateConfirmedAt(confirmationToken.getToken(), LocalDateTime.now());

        return confirmationToken;
    }

    public void deleteConfirmationToken(Account account) {
        List<ConfirmationToken> tokens = confirmationTokenRepository.findByAccount(account);
        confirmationTokenRepository.deleteAll(tokens);
    }
}
