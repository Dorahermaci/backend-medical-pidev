package tn.esprit.spring.Chat;

import lombok.*;
import tn.esprit.spring.AppUser.AppUser;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor


public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private AppUser sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private AppUser recipient;

    private String message;
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    private String sessionId;


    // constructors, getters and setters
}
