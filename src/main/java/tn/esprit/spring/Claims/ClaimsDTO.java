package tn.esprit.spring.Claims;

import tn.esprit.spring.AppUser.AppUserDTO;

import java.util.Date;

public class ClaimsDTO {

    private Long id;
    private String description;
    private Date date;

    private AppUserDTO appUser;
    private Claims.ClaimStatus status;
    private String comments;

    // Constructors
    public ClaimsDTO() {}

    public ClaimsDTO(Long id, String description, Date date, AppUserDTO appUser,
                     Claims.ClaimStatus status, String comments) {
        this.id = id;
        this.description = description;
        this.date = date;

        this.appUser = appUser;
        this.status = status;
        this.comments = comments;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /*public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }*/

    public AppUserDTO getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUserDTO appUser) {
        this.appUser = appUser;
    }

    public Claims.ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(Claims.ClaimStatus status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
