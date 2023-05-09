package tn.esprit.spring.MedicalFolder;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserRole;
import tn.esprit.spring.util.mappers.MedicalFolderMapper;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/medical-folders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MedicalFolderController {

    private final MedicalFolderService medicalFolderService;

    public MedicalFolderController(MedicalFolderService medicalFolderService) {
        this.medicalFolderService = medicalFolderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalFolder> getMedicalFolder(@PathVariable Long id) {
        MedicalFolder medicalFolder = medicalFolderService.getMedicalFolderById(id);
        if (medicalFolder != null) {
            return ResponseEntity.ok(medicalFolder);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MedicalFolderDTO>> getAllMedicalFolders() {
        List<MedicalFolder> medicalFolders = medicalFolderService.getAllMedicalFolders();
        if (medicalFolders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<MedicalFolderDTO> medicalFolderDTOs = medicalFolders.stream().map(MedicalFolderMapper::toDTO).toList();
        return ResponseEntity.ok(medicalFolderDTOs);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMedicalFolder(@RequestParam("medicalFolder") String medicalFolder,
                                                 @RequestParam("files") List<MultipartFile> files) {
        ObjectMapper objectMapper = new ObjectMapper();
        MedicalFolder medicalFolderObject;

        try {
            medicalFolderObject = objectMapper.readValue(medicalFolder, MedicalFolder.class);
        } catch (IOException e) {
            throw new RuntimeException("Invalid MedicalFolder JSON data", e);
        }

        MedicalFolder savedMedicalFolder = medicalFolderService.createMedicalFolder(medicalFolderObject, files);
        var retVal = new HashMap<String, String>();
        retVal.put("id", savedMedicalFolder.getId().toString());
        return ResponseEntity.created(URI.create("/medical-folders/" + savedMedicalFolder.getId())).body(retVal);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMedicalFolder(@PathVariable Long id,
                                                 @RequestParam("medicalFolder") String medicalFolder,
                                                 @RequestParam(name = "files", required = false) List<MultipartFile> files) {
        MedicalFolder existingMedicalFolder = medicalFolderService.getMedicalFolderById(id);
        if (existingMedicalFolder == null) {
            return ResponseEntity.notFound().build();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        MedicalFolder medicalFolderObject;

        try {
            medicalFolderObject = objectMapper.readValue(medicalFolder, MedicalFolder.class);
        } catch (IOException e) {
            throw new RuntimeException("Invalid MedicalFolder JSON data", e);
        }

        medicalFolderObject.setId(id); // set the id of the medicalFolderObject to the id passed in the path variable

        if (files != null && !files.isEmpty()) {
            medicalFolderObject.setMedicalFiles(new ArrayList<>());
            medicalFolderService.createMedicalFolder(medicalFolderObject, files);
        }

        if(files == null){
            files = new ArrayList<>();
        }

        MedicalFolder updatedMedicalFolder = medicalFolderService.saveOrUpdateMedicalFolder(medicalFolderObject, files);

        MedicalFolderDTO medicalFolderDTO = MedicalFolderMapper.toDTO(updatedMedicalFolder);
        return ResponseEntity.ok(medicalFolderDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalFolder(@PathVariable Long id) {
        MedicalFolder medicalFolder = medicalFolderService.getMedicalFolderById(id);
        if (medicalFolder == null) {
            return ResponseEntity.notFound().build();
        }
        medicalFolderService.deleteMedicalFolder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("findbyRole")
    public ResponseEntity<List<AppUser>> getAppUsersByRole(@RequestParam("role") String role) {
        try {
            AppUserRole appUserRole = AppUserRole.valueOf(role.toUpperCase());
            List<AppUser> appUsers = medicalFolderService.findByRole(appUserRole);
            if (appUsers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(appUsers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

