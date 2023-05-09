package tn.esprit.spring.MedicalFolder;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserRole;

import java.util.List;

public interface MedicalFolderService {
    MedicalFolder saveOrUpdateMedicalFolder(MedicalFolder medicalFolder, List<MultipartFile> files);
    void deleteMedicalFolder(Long id);
    MedicalFolder getMedicalFolderById(Long id);
    List<MedicalFolder> getAllMedicalFolders();
    List<MedicalFolder> getAllMedicalFoldersByPatient(AppUser patient);
    List<MedicalFolder> getAllMedicalFoldersByDoctor(AppUser doctor);
    MedicalFolder createMedicalFolder(MedicalFolder medicalFolder, List<MultipartFile> files);
    List<AppUser> findByRole (AppUserRole role);
}