package org.fundraiser.repository;

import org.fundraiser.domain.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("select a from Account a where a.username=:username")
    Optional<Account> findAccountByUserName(String username);

    @Query("select case when (count (a) > 0) then true else false end from Account a where a.username=:username")
    boolean userNameIsExist(String username);

    @Query("select case when (count (a) > 0) then true else false end from Account a where a.email=:email")
    boolean emailIsExist(String email);

    @Query("select a from Account a where a.accountType = 'ROLE_CREATOR' order by a.username")
    Page<Account> findCreatorAccount(Pageable pageable);

    @Query("select a from Account a where a.email=:email")
    Optional<Account> findAccountByEmail(String email);

}
