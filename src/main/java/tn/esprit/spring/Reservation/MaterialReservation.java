package tn.esprit.spring.Reservation;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import tn.esprit.spring.Material.Material;
import tn.esprit.spring.MedicalUser.MedicalStaff;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class MaterialReservation implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idReservation;
    private LocalDate date;
    private Integer quantity;

    //relation reservation&materiel
    @JsonIgnore
    @ManyToOne
    Material material;

    //relation reservation&medicalStaff
    @JsonIgnore
    @ManyToOne
    MedicalStaff medicalStaff;


}
