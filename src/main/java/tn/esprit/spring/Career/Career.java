package tn.esprit.spring.Career;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.Trainer.Trainers;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class Career implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonProperty("idCareer")
    private Long id;
    private String Speciality;
    private Integer YearsExprience ;
    private String University;



    @JsonIgnore
    @ManyToOne
    @JoinColumn(
            nullable = true,
            name="app_user_id"
    )
    private AppUser appUser;

    //relation career&medicalStaff

    //relation career&trainers
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainers trainer;


    public void setIdCareer(Long idCareer) {
    }
}
