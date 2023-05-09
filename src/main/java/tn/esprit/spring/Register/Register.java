package tn.esprit.spring.Register;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.TrainingCourse.TrainingCourse;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Register implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idRegister ;
    private Boolean completionStatus ;
    private LocalDateTime StartDate;
    private LocalDateTime EndDate;



@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "training_course_id")
    private TrainingCourse trainingCourse;



}
