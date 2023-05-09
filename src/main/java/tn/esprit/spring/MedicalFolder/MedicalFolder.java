package tn.esprit.spring.MedicalFolder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import tn.esprit.spring.AppUser.AppUser;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MedicalFolder {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String hospitalName;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date creationDate;
    @LastModifiedDate
    @Column(nullable = false)
    private Date lastModifiedDate;

    @OneToMany(mappedBy = "medicalFolder", cascade = CascadeType.ALL)
    private List<MedicalFile> medicalFiles = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private AppUser patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private AppUser doctor;

    public MedicalFolder(String title, String hospitalName, AppUser patient) {
        this.title = title;
        this.hospitalName = hospitalName;
        this.patient = patient;
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = new Date();
    }

    @PrePersist
    protected void onCreation() {
        creationDate = new Date();
        lastModifiedDate = new Date();
    }
}
