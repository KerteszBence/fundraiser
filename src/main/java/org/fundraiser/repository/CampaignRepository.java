package org.fundraiser.repository;

import org.fundraiser.config.CampaignCategory;
import org.fundraiser.domain.Account;
import org.fundraiser.domain.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    @Query("select c from Campaign c where c.category=:category and c.isOpen = true order by c.created desc ")
    Page<Campaign> findAllByCategory(CampaignCategory category, Pageable pageable);

    @Query("select c from Campaign c where c.account=:account and c.isOpen = true order by c.created desc ")
    Page<Campaign> findCampaignByAccount(Account account, Pageable pageable);

    @Query("select c from Campaign c where c.isOpen = true order by c.created desc ")
    Page<Campaign> findAllIsOpen(Pageable pageable);

    @Query("SELECT c FROM Campaign c WHERE c.isOpen = true AND c.expirationDate < :now")
    List<Campaign> findActiveExpiredCampaigns(@Param("now") LocalDateTime now); //kell a @Param("now"), mert a querryben nevezett paramétert küldünk be

    @Query("SELECT c FROM Campaign c WHERE c.target <= c.collected")
    List<Campaign> findCampaignsWithMetGoal();

}
