package tn.esprit.spring.MedicalFolder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.spring.AppUser.AppUserDTO;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MedicalFolderDTO {

    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Hospital name cannot be blank")
    private String hospitalName;

    private Date creationDate;
    private Date lastModifiedDate;

    private List<MedicalFile> medicalFiles = new ArrayList<>();

    private AppUserDTO patient;

    private AppUserDTO doctor;

    public MedicalFolderDTO(String title, String hospitalName, AppUserDTO patient, AppUserDTO doctor) {
        this.title = title;
        this.hospitalName = hospitalName;
        this.patient = patient;
        this.doctor = doctor;
    }
}
