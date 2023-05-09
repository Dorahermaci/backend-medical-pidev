package tn.esprit.spring.Appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserService;
import tn.esprit.spring.MedicalFolder.MedicalFolder;
import tn.esprit.spring.MedicalFolder.MedicalFolderDTO;
import tn.esprit.spring.MedicalFolder.MedicalFolderService;
import tn.esprit.spring.util.mappers.MedicalFolderMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private MedicalFolderService medicalFolderService;

    @Autowired
    private AppUserService appUserService;




    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> medicalFolders = appointmentService.getAllAppointments();
        if (medicalFolders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(medicalFolders);
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        MedicalFolder optionalMedicalFolder = medicalFolderService.getMedicalFolderById(appointmentDTO.getMedicalFolderId());
        Appointment appointment = appointmentService.createAppointment(optionalMedicalFolder, appointmentDTO.getStartTime(), appointmentDTO.getEndTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<Appointment> readAppointment(@PathVariable Long appointmentId) {
        Optional<Appointment> optionalAppointment = appointmentService.readAppointment(appointmentId);
        return optionalAppointment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long appointmentId, @RequestBody AppointmentDTO appointmentDTO) {
        Appointment updatedAppointment = appointmentService.updateAppointment(appointmentId, appointmentDTO.getStartTime(), appointmentDTO.getEndTime());
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> getAppointmentsByDoctorId(@PathVariable Long doctorId) {

        return appointmentService.getAppointmentsByDoctorId(doctorId);
    }

    @GetMapping("/patient/{patientId}")
    public List<Appointment> getAppointmentsByPatientId(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsByPatientId(patientId);
    }

    @GetMapping("/getUser/{userId}")
    public AppUser getUserById(@PathVariable Long userId) {
        return appUserService.getUserByID(userId);
    }


}
