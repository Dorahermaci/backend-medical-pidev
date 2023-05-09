package tn.esprit.spring.Trainer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import tn.esprit.spring.Career.Career;
import tn.esprit.spring.TrainingCourse.TrainingCourse;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class Trainers implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonProperty("idTrainer")
    private Long id;
    private String Name;
    private String Lastname;
    private Integer phoneNumber;
    @Email(message = "this must be an email form")
    private String email;
    private String Speciality;
    private String image ;

    @JsonIgnore
    //relation career&trainers
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "trainer")
    private List<Career> careers;

    //relation trainingCourse&trainers
    @JsonIgnore
    @ManyToMany(mappedBy = "trainers")
    private List<TrainingCourse> trainingCourses ;



}
