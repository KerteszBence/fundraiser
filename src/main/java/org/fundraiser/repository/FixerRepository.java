package org.fundraiser.repository;

import org.fundraiser.domain.EuroExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FixerRepository extends JpaRepository<EuroExchangeRate, Long> {
    @Query("select e from EuroExchangeRate e order by e.lastUpdated desc")
    List<EuroExchangeRate> getEuroExchangeRate();

}
