package tn.esprit.spring.MedicalUser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import tn.esprit.spring.Reservation.MaterialReservation;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class MedicalStaff implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer idStaff;
    private String firstName;
    private String lastName;
    private Integer nationalIdCard;
    private LocalDate dateOfBirth;
    private Integer phoneNumber;
    private String email;
    private String job;
    //@Enumerated(EnumType.STRING)
    //private Role role;


    //relation medicalFolder&medicalStaff
    //@ManyToMany(cascade = CascadeType.ALL)
    //private Set<MedicalFolder> medicalFolderSet;

    //relation appoitment&medicalStaff
    //@OneToMany(cascade = CascadeType.ALL)
    //private Set<Appointment> appointmentSet;

    //relation reservation&medicalStaff
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy="medicalStaff")
    private Set<MaterialReservation> materialReservationSet;

}
