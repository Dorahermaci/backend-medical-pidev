package tn.esprit.spring.util.mappers;

import tn.esprit.spring.MedicalFolder.MedicalFolder;
import tn.esprit.spring.MedicalFolder.MedicalFolderDTO;
import tn.esprit.spring.util.ObjectMapperUtil;

public class MedicalFolderMapper {

    public static MedicalFolderDTO toDTO(MedicalFolder medicalFolder) {
        MedicalFolderDTO dto = new MedicalFolderDTO();
        dto.setId(medicalFolder.getId());
        dto.setTitle(medicalFolder.getTitle());
        dto.setHospitalName(medicalFolder.getHospitalName());
        dto.setCreationDate(medicalFolder.getCreationDate());
        dto.setLastModifiedDate(medicalFolder.getLastModifiedDate());
        dto.setMedicalFiles(medicalFolder.getMedicalFiles());
        dto.setPatient(AppUserMapper.toDTO(medicalFolder.getPatient()));
        dto.setDoctor(AppUserMapper.toDTO(medicalFolder.getDoctor()));
        return dto;
    }

    public static MedicalFolder toEntity(MedicalFolderDTO medicalFolderDTO) {
        MedicalFolder entity = new MedicalFolder();
        entity.setId(medicalFolderDTO.getId());
        entity.setTitle(medicalFolderDTO.getTitle());
        entity.setHospitalName(medicalFolderDTO.getHospitalName());
        entity.setCreationDate(medicalFolderDTO.getCreationDate());
        entity.setLastModifiedDate(medicalFolderDTO.getLastModifiedDate());
        entity.setMedicalFiles(medicalFolderDTO.getMedicalFiles());
        entity.setPatient(AppUserMapper.toEntity(medicalFolderDTO.getPatient()));
        entity.setDoctor(AppUserMapper.toEntity(medicalFolderDTO.getDoctor()));

        return entity;
    }
}