package tn.esprit.spring.MedicalUser;


import java.util.List;

public interface IMedicalStaffService {

    public List<MedicalStaff> retrieveAllMedicalStaffs();

    public MedicalStaff updateMedicalStaff (MedicalStaff medicalStaff);

    public MedicalStaff addMedicalStaff (MedicalStaff medicalStaff);


    public MedicalStaff retrieveMedicalStaff (Integer  idStaff);

    public void removeMedicalStaff(Integer idStaff);

}
