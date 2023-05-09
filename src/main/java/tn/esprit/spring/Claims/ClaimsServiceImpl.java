package tn.esprit.spring.Claims;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserDTO;
import tn.esprit.spring.AppUser.AppUserRepository;
import tn.esprit.spring.AppUser.AppUserRole;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class ClaimsServiceImpl implements ClaimsService {

    @Autowired
    private ClaimsRepository claimsRepository;

    @Autowired
    private AppUserRepository appUserRepository;
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ClaimsDTO addOrUpdateClaim(ClaimsDTO claimsDTO, Long patientId) throws InvalidClaimException {
        // Check if patient exists
        AppUser patient = appUserRepository.findByIdAndAppUserRole(patientId, AppUserRole.PATIENT)
                .orElseThrow(() -> new InvalidClaimException("Invalid patient ID"));

        // Map DTO to entity
        Claims claim = modelMapper.map(claimsDTO, Claims.class);

        // Set patient and status
        claim.setAppUser(patient);
        claim.setStatus(Claims.ClaimStatus.PENDING);

        // Check description for bad words using Purgomalum API
        String description = claimsDTO.getDescription();
        String purgomalumUrl = "https://www.purgomalum.com/service/containsprofanity?text=" + description;
        try {
            URL url = new URL(purgomalumUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if (Boolean.parseBoolean(response.toString())) {
                    claim.setStatus(Claims.ClaimStatus.REJECTED);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save claim
        Claims savedClaim = claimsRepository.save(claim);

        // Map entity back to DTO and return
        return modelMapper.map(savedClaim, ClaimsDTO.class);
    }



    @Transactional
    @Override
    public ClaimsDTO getClaimsById(Long idClaims) {
        Claims claims = claimsRepository.findById(idClaims)
                .orElse(null) ;

        if (claims.getAppUser().getAppUserRole() == AppUserRole.LabrotoryManager) {
            claims.setStatus(Claims.ClaimStatus.IN_PROGRESS);
            claimsRepository.save(claims);
        }

        return new ClaimsDTO(claims.getIdClaims(), claims.getDescription(), claims.getDate(),
                new AppUserDTO(claims.getAppUser()), claims.getStatus(), claims.getComments());
    }


    @Override
    public List<ClaimsDTO> getAllClaims() {
        List<Claims> claimsList = claimsRepository.findAll();
        return claimsList.stream()
                .map(claim -> modelMapper.map(claim, ClaimsDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClaimsDTO> getClaimsByPatient(Long patientId) throws InvalidClaimException {
        // Check if patient exists
        AppUser patient = appUserRepository.findByIdAndAppUserRole(patientId, AppUserRole.PATIENT)
                .orElseThrow(() -> new InvalidClaimException("Invalid patient ID"));

        List<Claims> claimsList = claimsRepository.findByAppUserAndAppUserAppUserRole(patient, AppUserRole.PATIENT);
        return claimsList.stream()
                .map(claim -> modelMapper.map(claim, ClaimsDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ClaimsDTO getClaimById(Long claimId) throws InvalidClaimException {
        Claims claim = claimsRepository.findById(claimId)
                .orElseThrow(() -> new InvalidClaimException("Invalid claim ID"));

        return modelMapper.map(claim, ClaimsDTO.class);
    }

    @Override
    public ClaimsDTO updateClaimStatus(Long claimId, Claims.ClaimStatus status, String comments) throws InvalidClaimException {
        Claims claim = claimsRepository.findById(claimId)
                .orElseThrow(() -> new InvalidClaimException("Invalid claim ID"));

        claim.setStatus(status);
        claim.setComments(comments);

        Claims updatedClaim = claimsRepository.save(claim);
        return modelMapper.map(updatedClaim, ClaimsDTO.class);
    }
    public ClaimsDTO archiveClaim(Long id) throws InvalidClaimException{
        Claims claim = claimsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Claim not found"));

        if (claim.getStatus() != Claims.ClaimStatus.APPROVED) {
            throw new InvalidClaimException("Claim status must be APPROVED to archive the claim");
        }

        claim.setStatus(Claims.ClaimStatus.COMPLETED);
        claim.setDate(new Date()); // add a timestamp for when it was completed

        Claims archivedClaim = claimsRepository.save(claim);

        return modelMapper.map(archivedClaim, ClaimsDTO.class);
    }


}



