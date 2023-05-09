package tn.esprit.spring.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import tn.esprit.spring.Material.Material;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class Supplier implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer idSupplier;
    private String supplierName;
    private String adress;
    private Integer phoneNumber;
    @Column(length = 1000)
    private String qrContent;
    private byte[] qrCodeImage;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Material> material;

    public void setQrCodeImage(byte[] qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }


}
