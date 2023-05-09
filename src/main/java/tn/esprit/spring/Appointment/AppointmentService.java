package tn.esprit.spring.Appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserRepository;
import tn.esprit.spring.MedicalFolder.MedicalFolder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private AppUserRepository userRepository;

    public boolean isTimeSlotFree(MedicalFolder medicalFolder, Date startTime, Date endTime) {
        AppUser patient = medicalFolder.getPatient();

        AppUser doctor = medicalFolder.getDoctor();
        List<Appointment> toBeCheckedAppointments = appointmentRepository.findOverlappingAppointments(patient.getId(), doctor.getId());

        for (Appointment appointment : toBeCheckedAppointments) {
            if (isOverlapping(appointment.getStartTime(), appointment.getEndTime(), startTime, endTime)) {
                return false;
            }
        }
        return true;
    }

    private boolean isOverlapping(Date start1, Date end1, Date start2, Date end2) {
        return start1.compareTo(end2) <= 0 && end1.compareTo(start2) >= 0;
    }

    public Appointment createAppointment(MedicalFolder medicalFolder, Date startTime, Date endTime) {
        if (!isTimeSlotFree(medicalFolder, startTime, endTime)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment time slot conflicts with existing appointments.");
        }

        Appointment appointment = new Appointment();
        appointment.setMedicalFolder(medicalFolder);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setStatus(AppointmentEnum.PENDING);

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long appointmentId, Date startTime, Date endTime) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);

        if (!optionalAppointment.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found.");
        }

        Appointment appointment = optionalAppointment.get();
        MedicalFolder medicalFolder = appointment.getMedicalFolder();

        if (!isTimeSlotFree(medicalFolder, startTime, endTime)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment time slot conflicts with existing appointments.");
        }

        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);

        return appointmentRepository.save(appointment);
    }

    public Optional<Appointment> readAppointment(Long appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    public void cancelAppointment(Long appointmentId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);

        if (!optionalAppointment.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found.");
        }

        Appointment appointment = optionalAppointment.get();
        appointment.setStatus(AppointmentEnum.CANCELLED);

        appointmentRepository.deleteById(appointmentId);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }



    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {

        return appointmentRepository.findByMedicalFolderDoctor(userRepository.findById(doctorId));
    }


    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByMedicalFolderPatient(userRepository.findById(patientId));
    }
}
