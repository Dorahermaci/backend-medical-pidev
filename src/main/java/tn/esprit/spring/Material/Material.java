package tn.esprit.spring.Material;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import tn.esprit.spring.Reservation.MaterialReservation;
import tn.esprit.spring.Supplier.Supplier;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Material implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idMaterial;
    private String code;
    private String description;
    private Integer quantity;
    private Integer price;

    // relation material&materialUnderRepair
    //@ManyToMany(cascade = CascadeType.ALL)
    //private Set<MaterialUnderRepair> materialUnderRepairSet;

    // relation material&supplier
    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy="material", cascade = CascadeType.ALL)
    private Set<Supplier> suppliers;

    //relation material&reservation
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy="material")
    private Set<MaterialReservation> materialReservationSet;

}
