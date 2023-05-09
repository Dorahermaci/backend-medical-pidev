package tn.esprit.spring.Certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.TrainingCourse.TrainingCourse;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class Certificate implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idCertificate;
    @NotNull
    private String studentName;
    @NotNull
    private String title;
    @NotNull
    private String Description ;
    private Date ExpirationDate;




    //relation certificate&medicalStaff

    @JsonIgnore
    @ManyToOne
    @JoinColumn(
            nullable = true,
            name="app_user_id"
    )
    private AppUser appUser;

    //relation certificate&trainingCourse
    @JsonIgnore
    @JoinColumn(name = "training_course_id")
    @ManyToOne(cascade = CascadeType.ALL)
    TrainingCourse trainingCourse;



}
