package tn.esprit.spring.AppUser;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Getter
@Setter
public class UserStatisticsDTO {

    private long totalDoctors;
    private long totalPatients;
    private int patientsByDoctors;

    public UserStatisticsDTO(long totalDoctors, long totalPatients, int patientsByDoctors) {

        this.totalDoctors = totalDoctors;
        this.totalPatients = totalPatients;
        this.patientsByDoctors = patientsByDoctors;
    }
}
