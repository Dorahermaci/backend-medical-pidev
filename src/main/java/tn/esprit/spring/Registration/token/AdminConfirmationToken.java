package tn.esprit.spring.Registration.token;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.spring.AppUser.AppUser;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class AdminConfirmationToken {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String admintoken;
    @Column(nullable = false)
    private LocalDateTime CreatedAt;
    @Column(nullable = false)
    private LocalDateTime expiredAt;
    private LocalDateTime confirmedAt;
    @ManyToOne
    @JoinColumn(
            nullable = false,
            name="app_user_id"
    )
    private AppUser appUser;

    public AdminConfirmationToken(String admintoken, LocalDateTime createdAt, LocalDateTime expiredAt, AppUser appUser) {
        this.admintoken = admintoken;
        this.CreatedAt = createdAt;
        this.expiredAt = expiredAt;
        this.appUser = appUser;
    }

    public String getAdmintoken() {
        return admintoken;
    }
}
