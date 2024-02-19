package org.fundraiser.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "euro_exchange_rate")
@Data
@NoArgsConstructor
public class EuroExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Double EUR;
    private Double USD;
    private Double HUF;
    private LocalDateTime lastUpdated;
}
