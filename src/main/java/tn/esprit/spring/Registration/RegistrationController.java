package tn.esprit.spring.Registration;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.AppUser.*;
import tn.esprit.spring.Registration.token.PasswordResetToken;
import tn.esprit.spring.Registration.token.PasswordResetTokenService;
import tn.esprit.spring.email.EmailSender;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    @Autowired
    private JwtService jwtService;
    private RegistrationService registrationService;


    private AppUserService appUserService;




    @PostMapping("/checkSMS")
    public UserAccountResponse CheckSMS (@RequestBody UserResetPasswordSMS userResetPasswordSMS) {
        return appUserService.CheckSMS(userResetPasswordSMS);
    }
    @PostMapping("/resetPasswordSMS")
    public UserAccountResponse resetPasswordSMS (@RequestBody UserNewPasswordSMS newPassword) {
        return appUserService.resetPasswordSMS(newPassword);
    }

    @PostMapping("/login")
    public String createJwtToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        return jwtService.createJwtToken(jwtRequest);
    }

    @PostMapping
    public String register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }



    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token) {

        return registrationService.confirmToken(token);
    }

    @GetMapping(path = "confirmadmin")
    public String confirmadmin(@RequestParam("token") String admintoken) {

        return registrationService.confirmadminToken(admintoken);
    }


    @GetMapping("/count")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("doctors", appUserService.countDoctors());
        stats.put("patients", appUserService.countPatients());
        return stats;
    }


    @GetMapping("/statistics")
    public Object getUserStatistics() {
        UserStatisticsDTO userStatistics = appUserService.getUserStatistics();
        if (userStatistics.getTotalPatients() % 2 != 0) {
            return ("Number of patients is odd");
        }
        String result = "There are " + userStatistics.getTotalDoctors() + " doctors and " +
                userStatistics.getTotalPatients() + " patients. " +
                "The number of patients per doctor is " + userStatistics.getPatientsByDoctors();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetRequest resetRequest) {
        appUserService.resetPassword(resetRequest);
        return ResponseEntity.ok("Password reset successfully.");
    }
    @PostMapping("/password-generate")
    public ResponseEntity<?> generatePasswordResetToken(@RequestBody PasswordResetRequest request) {
        String token = appUserService.generatePasswordResetToken(request.getEmail());
        String message = "Check your email for password reset token " ;
        return ResponseEntity.ok(message);
    }






}
