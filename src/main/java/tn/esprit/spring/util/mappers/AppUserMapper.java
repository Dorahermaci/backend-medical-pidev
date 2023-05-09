package tn.esprit.spring.util.mappers;

import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserDTO;

public class AppUserMapper {

    public static AppUserDTO toDTO(AppUser appUser) {
        AppUserDTO dto = new AppUserDTO();
        dto.setId(appUser.getId());
        dto.setEmail(appUser.getEmail());
        dto.setFirstName(appUser.getFirstName());
        dto.setLastName(appUser.getLastName());
        return dto;
    }

    public static AppUser toEntity(AppUserDTO appUserDTO) {
        AppUser entity = new AppUser();
        entity.setId(appUserDTO.getId());
        entity.setEmail(appUserDTO.getEmail());
        entity.setFirstName(appUserDTO.getFirstName());
        entity.setLastName(appUserDTO.getLastName());
        return entity;
    }
}