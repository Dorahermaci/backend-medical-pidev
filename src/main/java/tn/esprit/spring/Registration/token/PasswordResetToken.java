package tn.esprit.spring.Registration.token;

import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.*;
import tn.esprit.spring.AppUser.AppUser;


@Entity
@Getter
@Setter
@NoArgsConstructor

public class PasswordResetToken {

    private static final int EXPIRATION_TIME_IN_MINUTES = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    private LocalDateTime expiryDate;

    public PasswordResetToken(String token, LocalDateTime expiryDate, AppUser user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_TIME_IN_MINUTES);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    public AppUser getAppUser() {
        return user;
    }


}
