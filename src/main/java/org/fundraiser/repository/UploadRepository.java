package org.fundraiser.repository;

import org.fundraiser.domain.Account;
import org.fundraiser.domain.FileRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadRepository extends JpaRepository<FileRegistry, Long> {

    @Query("select f from FileRegistry f where f.account=:account")
    List<FileRegistry> getFilesByAccount(Account account);

}
