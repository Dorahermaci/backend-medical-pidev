package tn.esprit.spring.MedicalFolder;


import java.util.List;

public interface MedicalFileService {
    MedicalFile saveOrUpdateMedicalFile(MedicalFile medicalFile);
    void deleteMedicalFile(Long id);
    MedicalFile getMedicalFileById(Long id);
    List<MedicalFile> getAllMedicalFiles();
    List<MedicalFile> getAllMedicalFilesByMedicalFolder(MedicalFolder mf);
}
