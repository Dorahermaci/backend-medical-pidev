package tn.esprit.spring.MedicalFolder;


import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserRepository;
import tn.esprit.spring.AppUser.AppUserRole;
import tn.esprit.spring.Exception.ResourceNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
public class MedicalFolderServiceImpl implements MedicalFolderService {

    @Autowired
    private MedicalFolderRepository medicalFolderRepository;
    @Autowired
    private AppUserRepository appUserRepository;



    @Override
    public void deleteMedicalFolder(Long id) {
        medicalFolderRepository.deleteById(id);
    }

    @Override
    public MedicalFolder getMedicalFolderById(Long id) {
        return medicalFolderRepository.findById(id).orElse(null);
    }

    @Override
    public List<MedicalFolder> getAllMedicalFolders() {
        return medicalFolderRepository.findAll();
    }

    @Override
    public List<MedicalFolder> getAllMedicalFoldersByPatient(AppUser patient) {
        return medicalFolderRepository.findByPatient(patient);
    }

    @Override
    public List<MedicalFolder> getAllMedicalFoldersByDoctor(AppUser doctor) {
        return medicalFolderRepository.findByDoctor(doctor);
    }

    @Override
    @SneakyThrows
    public MedicalFolder createMedicalFolder(MedicalFolder medicalFolder, List<MultipartFile> files) {
        AppUser patient = appUserRepository.findById(medicalFolder.getPatient().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient id " + medicalFolder.getPatient().getId()));
        medicalFolder.setPatient(patient);

        String uploadDir = "src/main/resources/static/uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        for (MultipartFile file : files) {
            if (!file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                throw new IllegalArgumentException("Only PDF files are allowed.");
            }

            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/" + fileName;
            MedicalFile medicalFile = new MedicalFile(fileName, "pdf", fileUrl, medicalFolder.getPatient(), medicalFolder.getDoctor(), medicalFolder);
            medicalFolder.getMedicalFiles().add(medicalFile);
        }

        return medicalFolderRepository.save(medicalFolder);
    }

    @Override
    @SneakyThrows
    public MedicalFolder saveOrUpdateMedicalFolder(MedicalFolder medicalFolder, List<MultipartFile> files) {
        String uploadDir = "src/main/resources/static/uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        for (MultipartFile file : files) {
            if (!Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".pdf")) {
                throw new IllegalArgumentException("Only PDF files are allowed.");
            }

            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/" + fileName;
            MedicalFile medicalFile = new MedicalFile(fileName, "pdf", fileUrl, medicalFolder.getPatient(), medicalFolder.getDoctor(), medicalFolder);
            medicalFolder.getMedicalFiles().add(medicalFile);
        }
        medicalFolderRepository.deleteById(medicalFolder.getId());
        medicalFolderRepository.save(medicalFolder);
        return medicalFolder;
    }

    @Override
    public List<AppUser> findByRole(AppUserRole role) {
        return appUserRepository.findByRole(role);
    }
}
