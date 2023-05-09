package tn.esprit.spring.MedicalFolder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.MedicalFolder.MedicalFolder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicalFile {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastModifiedDate;

    private String fileName;
    private String fileType;
    private String fileUrl;

    @ManyToOne
    @JsonIgnore
    private AppUser patient;

    @ManyToOne
    @JsonIgnore
    private AppUser doctor;

    @ManyToOne
    @JsonIgnore
    private MedicalFolder medicalFolder;

    public MedicalFile(String fileName, String fileType, String fileUrl, AppUser patient, AppUser doctor, MedicalFolder medicalFolder) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileUrl = fileUrl;
        this.patient = patient;
        this.doctor = doctor;
        this.medicalFolder = medicalFolder;
        this.creationDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
    }
}
