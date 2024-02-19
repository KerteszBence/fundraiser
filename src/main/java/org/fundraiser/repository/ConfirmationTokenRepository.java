package org.fundraiser.repository;

import org.fundraiser.domain.Account;
import org.fundraiser.domain.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    @Modifying
    @Query("UPDATE ConfirmationToken c SET c.confirmedAt = ?2 WHERE c.token = ?1")
    void updateConfirmedAt(String token, LocalDateTime confirmedAt);

    Optional<ConfirmationToken> findByToken(String token);

    @Query("SELECT t FROM ConfirmationToken t where t.account=:account")
    List<ConfirmationToken> findByAccount(Account account);

}