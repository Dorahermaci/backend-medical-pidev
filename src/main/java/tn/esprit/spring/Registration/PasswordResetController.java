package tn.esprit.spring.Registration;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserService;
import tn.esprit.spring.Registration.token.PasswordResetToken;
import tn.esprit.spring.Registration.token.PasswordResetTokenService;
import tn.esprit.spring.email.EmailSender;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/password-reset")
@AllArgsConstructor
public class PasswordResetController {

    private final PasswordResetTokenService passwordResetTokenService;
    private final AppUserService appUserService;
    private final EmailSender emailSender;



}

