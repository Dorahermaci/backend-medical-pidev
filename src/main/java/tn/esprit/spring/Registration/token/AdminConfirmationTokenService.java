package tn.esprit.spring.Registration.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminConfirmationTokenService {

    private final AdminConfirmationTokenRepository adminconfirmationTokenRepository;

    public void saveConfirmationToken(AdminConfirmationToken admintoken){
        adminconfirmationTokenRepository.save(admintoken);
    }

    public Optional<AdminConfirmationToken> getAdmintoken(String admintoken) {
        return adminconfirmationTokenRepository.findByAdmintoken(admintoken);
    }

    public int setConfirmedAt(String admintoken) {
        return adminconfirmationTokenRepository.updateConfirmedAt(
                admintoken, LocalDateTime.now());
    }
}
