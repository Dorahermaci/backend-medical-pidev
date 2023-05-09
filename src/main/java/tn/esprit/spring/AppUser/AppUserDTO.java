package tn.esprit.spring.AppUser;

import lombok.Getter;
import lombok.Setter;
import tn.esprit.spring.Claims.ClaimsDTO;

@Getter
@Setter
public class AppUserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phonenumber;
    private Integer CIN;
    private String job;
    private AppUserRole appUserRole;
    private Boolean locked;
    private Boolean enabled;
    private ClaimsDTO claimsDTO;
    private String username;
    private String Code;

    public enum AppUserRole {
        USER, ADMIN, PATIENT, DOCTOR, LABORATORY_MANAGER
    }

    public AppUserDTO() {
    }
    public AppUserDTO(AppUser appUser) {
    }

    public AppUserDTO(Long id, String firstName, String lastName, String email, String password,
                      String phonenumber, Integer CIN, String job, AppUserRole appUserRole,
                      Boolean locked, Boolean enabled, ClaimsDTO claimsDTO) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phonenumber = phonenumber;
        this.CIN = CIN;
        this.job = job;
        this.appUserRole = appUserRole;
        this.locked = locked;
        this.enabled = enabled;
        this.claimsDTO = claimsDTO;
    }
}
