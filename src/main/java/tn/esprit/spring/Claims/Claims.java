package tn.esprit.spring.Claims;

import lombok.*;
import tn.esprit.spring.AppUser.AppUser;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class Claims implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idClaims;
    private String description;
    @Temporal(TemporalType.DATE)
    private Date date;



    
    @OneToOne(cascade = CascadeType.ALL)
    private AppUser appUser;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private String comments;

    public enum ClaimStatus {
        PENDING,
        IN_PROGRESS,
        APPROVED,
        REJECTED,
        COMPLETED
    }



}
