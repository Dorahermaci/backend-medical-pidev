package tn.esprit.spring.Claims;


//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ClaimsService {

    ClaimsDTO addOrUpdateClaim(ClaimsDTO claimsDTO, Long patientId) throws InvalidClaimException;

    ClaimsDTO getClaimsById(Long idClaims);

    List<ClaimsDTO> getAllClaims();
    List<ClaimsDTO> getClaimsByPatient(Long patientId) throws InvalidClaimException;
    ClaimsDTO getClaimById(Long claimId) throws InvalidClaimException;
    ClaimsDTO updateClaimStatus(Long claimId, Claims.ClaimStatus status, String comments) throws InvalidClaimException;
    ClaimsDTO archiveClaim(Long id)throws InvalidClaimException;
}