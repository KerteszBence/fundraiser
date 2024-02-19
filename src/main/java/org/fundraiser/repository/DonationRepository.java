package org.fundraiser.repository;

import org.fundraiser.domain.Donation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT d FROM Donation d WHERE d.source.username = :username ORDER BY d.id DESC")
    Page<Donation> findLastDonationBySourceUsername(@Param("username") String username, Pageable pageable);

    @Query("SELECT d FROM Donation d WHERE d.campaign.id = :campaignId AND d.refunded = false ORDER BY d.id")
    List<Donation> findAllCampaignDonations(@Param("campaignId") Long campaignId);

    @Query("SELECT d FROM Donation d WHERE d.transactionId = :transactionId")
    Donation findDonationByTransactionId(@Param("transactionId") String transactionId);

}
