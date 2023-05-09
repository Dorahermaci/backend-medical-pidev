package tn.esprit.spring.Claims;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/claims")
public class ClaimsController {
    @Autowired
    private ClaimsRepository claimsRepository;

    @Autowired
    private ClaimsService claimsService;

    @PostMapping("/{patientId}")
    public ResponseEntity<ClaimsDTO> addOrUpdateClaim(@RequestBody ClaimsDTO claimsDTO, @PathVariable Long patientId) {
        try {
            ClaimsDTO savedClaim = claimsService.addOrUpdateClaim(claimsDTO, patientId);
            return ResponseEntity.ok(savedClaim);
        } catch (InvalidClaimException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ClaimsDTO>> getAllClaims() {
        List<ClaimsDTO> claimsList = claimsService.getAllClaims();
        return ResponseEntity.ok(claimsList);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ClaimsDTO>> getClaimsByPatient(@PathVariable Long patientId) {
        try {
            List<ClaimsDTO> claimsList = claimsService.getClaimsByPatient(patientId);
            return ResponseEntity.ok(claimsList);
        } catch (InvalidClaimException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{claimId}")
    public ResponseEntity<ClaimsDTO> getClaimById(@PathVariable Long claimId) {
        try {
            ClaimsDTO claim = claimsService.getClaimById(claimId);
            return ResponseEntity.ok(claim);
        } catch (InvalidClaimException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/claims/{id}")
    public ResponseEntity<ClaimsDTO> getClaimsById(@PathVariable Long id) {
        ClaimsDTO claimsDTO = claimsService.getClaimsById(id);
        if (claimsDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(claimsDTO, HttpStatus.OK);
    }


    @PutMapping("/{claimId}")
    public ResponseEntity<ClaimsDTO> updateClaimStatus(@PathVariable Long claimId, @RequestParam Claims.ClaimStatus status, @RequestParam String comments) {
        try {
            ClaimsDTO updatedClaim = claimsService.updateClaimStatus(claimId, status, comments);
            return ResponseEntity.ok(updatedClaim);
        } catch (InvalidClaimException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/archive/{id}")
    public ResponseEntity<Claims> archiveClaim(@PathVariable Long id) throws InvalidClaimException{
        Claims claim = claimsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Claim not found"));

        if (claim.getStatus() != Claims.ClaimStatus.APPROVED) {
            throw new InvalidClaimException("Claim is not approved");
        }

        claim.setStatus(Claims.ClaimStatus.COMPLETED);
        claim.setDate(new Date()); // add a timestamp for when it was completed

        Claims archivedClaim = claimsRepository.save(claim);

        return ResponseEntity.ok(archivedClaim);
    }


}
