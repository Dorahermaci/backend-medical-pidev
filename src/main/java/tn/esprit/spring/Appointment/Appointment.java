package tn.esprit.spring.Appointment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tn.esprit.spring.MedicalFolder.MedicalFolder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
public class Appointment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Date startTime;
    private Date endTime;

    private AppointmentEnum status;


    @ManyToOne(cascade = CascadeType.MERGE)
    private MedicalFolder medicalFolder;


    private String description;

    private Date created_at;
    private Date updated_at;

    @PrePersist
    protected void onCreate() {

        created_at = new Date();
        updated_at = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = new Date();
    }
}
