package tn.esprit.spring.TrainingCourse;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.Certificate.Certificate;
import tn.esprit.spring.Rating.Rating;
import tn.esprit.spring.Trainer.Trainers;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class TrainingCourse implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "idCourse")
    private Long id;
    private String CourseName;
    private String domain;
    private String description;
    private String Duration ;
    private Date StartDate ;
    private Date EndDate ;
    private Long EstimatedTime ;
    private int likes;
    private int dislikes;
    private double AvgRate;




    @JsonIgnore
    @OneToMany(mappedBy = "trainingCourse")
    private List<Certificate> certificates;
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "trainer_training_course",
            joinColumns = @JoinColumn(name = "training_course_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id"))
    private List<Trainers> trainers ;
    @JsonIgnore
    @ManyToMany(mappedBy = "courses")
    private List<AppUser> users;
    @JsonIgnore
    @OneToMany(mappedBy = "trainingCourse", cascade = CascadeType.ALL)
    private List<Rating> ratings = new ArrayList<>();




}
