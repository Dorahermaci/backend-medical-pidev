package tn.esprit.spring.MedicalUser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class MedicalStaffServiceImpl implements IMedicalStaffService {

    @Autowired
    MedicalStaffRepository medicalStaffRepository;

    @Override
    public List<MedicalStaff> retrieveAllMedicalStaffs() {
        return medicalStaffRepository.findAll();
    }

    @Override
    public MedicalStaff updateMedicalStaff(MedicalStaff medicalStaff) {
        return medicalStaffRepository.save(medicalStaff);}

    @Override
    public MedicalStaff addMedicalStaff(MedicalStaff medicalStaff) {

        return medicalStaffRepository.save(medicalStaff);
    }

    @Override
    public MedicalStaff retrieveMedicalStaff(Integer idStaff) {
        return null;
    }

    @Override
    public void removeMedicalStaff(Integer idStaff) { medicalStaffRepository.deleteById(idStaff);}
}
