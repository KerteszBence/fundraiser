package org.fundraiser.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "donation")
@Data
@NoArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double balance;

    private String currency;

    @ManyToOne
    @JoinColumn(name = "transfer_from")
    @ToString.Exclude
    private Account source;

    @ManyToOne
    @JoinColumn(name = "transfer_to")
    @ToString.Exclude
    private Account target;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    @ToString.Exclude
    private Campaign campaign;

    private String paypalId;

    private String transactionId;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    private Boolean refunded;
}