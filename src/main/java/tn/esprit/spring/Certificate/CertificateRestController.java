package tn.esprit.spring.Certificate;

import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserRepository;
import tn.esprit.spring.Exception.ResourceNotFoundException;
import tn.esprit.spring.TrainingCourse.ITrainingCourseService;


import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/Certificate")
public class CertificateRestController {
    @Autowired
    private ICertificateService certificateService;
    @Autowired
    private ITrainingCourseService trainingCourseService;
    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    // Create a new certificate
    @PostMapping("createCertificate")
    public ResponseEntity<Certificate> createCertificate(@RequestBody @Valid Certificate certificate) {
        Certificate newCertificate = certificateService.addCertificate(certificate);
        return new ResponseEntity<>(newCertificate, HttpStatus.CREATED);
    }

    // Get all certificates
    @GetMapping("getAllCertificates")
    public ResponseEntity<List<Certificate>> getAllCertificates() {
        List<Certificate> certificates = certificateService.getAllCertificates();
        return new ResponseEntity<>(certificates, HttpStatus.OK);
    }

    // Get a certificate by ID
    @GetMapping("getCertificateById/{id}")
    public ResponseEntity<Certificate> getCertificateById(@PathVariable Long id) {
        Certificate certificate = certificateService.getCertificateById(id);
        if (certificate != null) {
            return new ResponseEntity<>(certificate, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update a certificate
    @PutMapping("Updatecertificate/{id}")
    public ResponseEntity<Certificate> updateCertificate(@PathVariable Long id, @RequestBody @Valid Certificate certificate) {
        certificate.setIdCertificate(id);
        Certificate updatedCertificate = certificateService.updateCertificate(certificate);
        if (updatedCertificate != null) {
            return new ResponseEntity<>(updatedCertificate, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a certificate
    @DeleteMapping("DeleteCertificate/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable Long id) {
        boolean isDeleted = certificateService.deleteCertificate(id);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{certificateId}/assign-to/{appUserId}")
    public ResponseEntity<String> assignCertificateToAppUser(
            @PathVariable("certificateId") Long certificateId,
            @PathVariable("appUserId") Long appUserId) {

        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        AppUser appUser = appUserRepository.findById(appUserId)
                .orElseThrow(() -> new ResourceNotFoundException("AppUser not found"));

        certificate.setAppUser(appUser);
        certificateRepository.save(certificate);

        return ResponseEntity.ok("Certificate assigned to AppUser successfully");
    }

    @PostMapping("assignCertificateToTrainingCourse/{certificateId}/assignToCourse/{courseId}")
    public ResponseEntity<String> assignCertificateToTrainingCourse(@PathVariable Long certificateId, @PathVariable Long courseId) {
        certificateService.assignCertificateToTrainingCourse(certificateId, courseId);
        return ResponseEntity.ok("Certificate assigned to training course successfully.");
    }


 @GetMapping("exportContrat/export/{id}")
 public ResponseEntity<Resource> exportContrat(@PathVariable Long id) throws IOException, DocumentException {

     String filename = "contract_" + id + ".pdf";
     String filePath = "C:/Users/MSI/Desktop/" + filename; // Update with your actual desktop path


     // Export the contract to PDF
     certificateService.exportCertificate(id, filePath);

     // Prepare the file as a Resource
     File file = new File(filePath);
     Path path = file.toPath();
     ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

     // Set the response headers
     HttpHeaders headers = new HttpHeaders();

     headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

     // Return the file as a ResponseEntity
     return ResponseEntity.ok()
             .headers(headers)
             .contentLength(file.length())
             .contentType(MediaType.parseMediaType("application/pdf"))
             .body(resource);

 }
    }



