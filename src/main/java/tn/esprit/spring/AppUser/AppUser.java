package tn.esprit.spring.AppUser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.esprit.spring.Appointment.Appointment;
import tn.esprit.spring.Career.Career;
import tn.esprit.spring.Certificate.Certificate;
import tn.esprit.spring.MedicalFolder.MedicalFolder;
import tn.esprit.spring.TrainingCourse.TrainingCourse;


import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity

public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false ,length = 20)
    private String firstName;
    @Column(nullable = false ,length = 20)
    private String lastName;
    @Column(nullable = false , unique = true , length = 45)
    private String email;
    @Column(nullable = false ,length = 64)
    private String password;

    private String Code;
    private String phonenumber;

    private int Score ;

    @Column(nullable = false , unique = true)
    private Integer CIN;

    private String job;

    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;
    private Boolean locked = true;
    private Boolean enabled = false;

    @JsonIgnore
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    private List<Career> careers;
    @JsonIgnore
    @OneToMany(mappedBy = "appUser")
    private List<Certificate> certificates;
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "Register",
            joinColumns = @JoinColumn(name = "app_user_id"),
            inverseJoinColumns = @JoinColumn(name = "training_course_id")
    )
    private List<TrainingCourse> courses;

    //Dorra


    @JsonIgnore
    @OneToMany(mappedBy = "patient")
    private List<MedicalFolder> medicalFolders;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    private List<MedicalFolder> managedMedicalFolders;

    @JsonIgnore
    @OneToMany
    private List<Appointment> appointments;


    public AppUser(String firstName, String lastName, String email, String password, AppUserRole appUserRole, String phonenumber,
                   Integer CIN, String job) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.appUserRole = appUserRole;
        this.phonenumber = phonenumber;
        this.CIN = CIN;
        this.job = job;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority= new SimpleGrantedAuthority(appUserRole.name());
        return Collections.singletonList(authority);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return email;
    }

    public void setUsername(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }

    public Integer getCIN() {
        return CIN;
    }

    public void setCIN(Integer CIN) {
        this.CIN = CIN;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public AppUserRole getAppUserRole() {
        return appUserRole;
    }

    public void setAppUserRole(AppUserRole appUserRole) {
        this.appUserRole = appUserRole;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Career> getCareers() {
        return careers;
    }

    public void setCareers(List<Career> careers) {
        this.careers = careers;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public List<TrainingCourse> getCourses() {
        return courses;
    }

    public void setCourses(List<TrainingCourse> courses) {
        this.courses = courses;
    }

    public List<MedicalFolder> getMedicalFolders() {
        return medicalFolders;
    }

    public void setMedicalFolders(List<MedicalFolder> medicalFolders) {
        this.medicalFolders = medicalFolders;
    }

    public List<MedicalFolder> getManagedMedicalFolders() {
        return managedMedicalFolders;
    }

    public void setManagedMedicalFolders(List<MedicalFolder> managedMedicalFolders) {
        this.managedMedicalFolders = managedMedicalFolders;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @OneToMany
    public List<Appointment> getAppointments() {
        return appointments;
    }
}
