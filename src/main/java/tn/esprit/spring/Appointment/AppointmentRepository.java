package tn.esprit.spring.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.AppUser.AppUser;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a WHERE " +
            "(a.medicalFolder.patient.id = :patientId OR a.medicalFolder.doctor.id = :doctorId) ")
    List<Appointment> findOverlappingAppointments(
            @Param("patientId") Long patientId,
            @Param("doctorId") Long doctorId);



    List<Appointment> findByMedicalFolderDoctor(Optional<AppUser> doctor);
    List<Appointment> findByMedicalFolderPatient(Optional<AppUser> patient);


}