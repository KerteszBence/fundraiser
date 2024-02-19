import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.fundraiser.domain.Campaign;
import org.fundraiser.domain.ConfirmationToken;
import org.fundraiser.domain.Donation;
import org.fundraiser.domain.FileRegistry;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(name = "profile_picture_id")
    private Long profilePictureId;

    @Column(name = "cover_picture_id")
    private Long coverPictureId;

    @Column(name = "paypal_account_email")
    private String paypalAccountEmail;

    private String paypalId;

    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @OneToMany(mappedBy = "account")
    private List<ConfirmationToken> tokens;

    @OneToMany(mappedBy = "account")
    private List<Campaign> campaigns;

    @OneToMany(mappedBy = "account")
    private List<FileRegistry> files;

    @OneToMany(mappedBy = "source")
    private List<Donation> outgoingDonations;

    @OneToMany(mappedBy = "target")
    private List<Donation> incomingDonations;

    @ManyToMany()
    private List<Account> followers;

    @ManyToMany()
    private List<Account> followings;
}