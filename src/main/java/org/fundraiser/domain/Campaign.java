package org.fundraiser.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.fundraiser.config.CampaignCategory;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "campaign")
@Data
@NoArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String introduction;

    private String projectDescription;

    private Long avatarId;

    private Long coverId;

    private Long introVideoId;

    private Double target;

    private String currency;

    private Double collected;

    private LocalDateTime created;

    private LocalDateTime expirationDate;

    @Column(name = "campaign_category")
    @Enumerated(EnumType.STRING)
    private CampaignCategory category;

    @Column(name = "like_counter")
    private Long likeCounter;

    @OneToMany(mappedBy = "campaign")
    private List<FileRegistry> files;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "campaign")
    private List<Donation> donations;

    @Column(name = "last_modification")
    private LocalDateTime lastModification;

    @Column(name = "is_open")
    private boolean isOpen;
}