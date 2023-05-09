package tn.esprit.spring.AppUser;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.Registration.PasswordResetRequest;
import tn.esprit.spring.Registration.RegistrationRequest;
import tn.esprit.spring.Registration.token.*;
import tn.esprit.spring.email.EmailSender;
import tn.esprit.spring.util.UserCode;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG =
            "user with email %s not found";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ConfirmationTokenService confirmationTokenService;

    private final AdminConfirmationTokenService adminConfirmationTokenService;
    private final EmailSender emailSender;

    @Autowired
    private SmsServiceImpl smsService;


    private final PasswordResetTokenRepository passwordResetTokenRepository;


    public AppUser getAppUserById(Long id) {
        Optional<AppUser> optionalAppUser = appUserRepository.findById(id);
        if (optionalAppUser.isPresent()) {
            return optionalAppUser.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User staff not found with id " + id);
        }
    }

    public void lockUser(String email) {
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));

        user.setLocked(true);
        appUserRepository.save(user);
    }

    public long countDoctors() {
        return appUserRepository.countByAppUserRole(AppUserRole.DOCTOR);
    }

    public long countPatients() {
        return appUserRepository.countByAppUserRole(AppUserRole.PATIENT);
    }

    public UserStatisticsDTO getUserStatistics() {
        int totalDoctors = 0;
        int totalPatients = 0;
        int patientsPerDoctors = 0;

        for (AppUser user : appUserRepository.findAll()) {
            if (user.getAppUserRole() == AppUserRole.DOCTOR) {
                totalDoctors++;
            } else if (user.getAppUserRole() == AppUserRole.PATIENT) {
                totalPatients++;
            }
        }

        if (totalDoctors > 0) {
            patientsPerDoctors = totalPatients / totalDoctors;
        }

        return new UserStatisticsDTO(totalDoctors, totalPatients, patientsPerDoctors);
    }







    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(AppUser appUser , RegistrationRequest request){
        boolean userExists = appUserRepository
                .findByEmail(appUser.getEmail())
                .isPresent();
        if (userExists){
            throw new IllegalStateException("email already taken");

        }

        appUser.setAppUserRole(request.getRole());
        String encodedPassword =
                bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);

        String admintoken = UUID.randomUUID().toString();
       //TODO: SEND confirmation token
        AdminConfirmationToken adminconfirmationToken = new AdminConfirmationToken(
                admintoken,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                appUser);

        adminConfirmationTokenService.saveConfirmationToken(adminconfirmationToken);

        String token = UUID.randomUUID().toString();
        //TODO: SEND confirmation token
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser);

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String linkadmin = "http://localhost:8091/api/v1/registration/confirmadmin?token=" + admintoken;
        emailSender.send(
                "mouhibbengayes7@gmail.com",buildEmailadmin(
                        request.getFirstName(),request.getLastName(), request.getCIN(),request.getPhonenumber(), linkadmin));

        String linkuser = "http://localhost:8091/api/v1/registration/confirm?token=" + token;
        emailSender.send(
                request.getEmail(),buildEmail(
                        request.getFirstName(),request.getLastName(),  linkuser));





        return admintoken;
    }


    public AppUser findByPhone(String phone)
    {
        return this.appUserRepository.findByPhonenumber(phone).get();
    }
    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
    public int enableFullAppUser(String email) {

        return appUserRepository.enableFullAppUser(email);
    }


    public String generatePasswordResetToken(String email) {
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));


        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, expiryDate, user);
        passwordResetTokenRepository.save(passwordResetToken);

        String resetLink = "This is your password reset token : " + token;
        emailSender.send(email, buildPasswordResetEmail(user.getFirstName(), resetLink));

        return token;
    }

    public void resetPassword(PasswordResetRequest request) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid password reset token"));

        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token has expired");
        }

        AppUser user = passwordResetToken.getAppUser();
        String encodedPassword = bCryptPasswordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        appUserRepository.save(user);

        passwordResetTokenRepository.delete(passwordResetToken);
    }

    private String buildPasswordResetEmail(String name, String resetLink) {
        return "Dear " + name + ",\n\n"
                + "We have received a request to reset your password. If you did not make this request, please ignore this email.\n\n"
                + "To reset your password, please copy this token :\n\n"
                + resetLink + "\n\n"
                + "This link is valid for the next 24 hours.\n\n"
                + "Best regards,\n"
                + "Charles Nicole Managament";
    }
//reset password Using SMS:

    //Check Phone number in DB and send 6 digits code.
    public UserAccountResponse CheckSMS (UserResetPasswordSMS userResetPasswordSMS) {
        // Retrieve user using the entered phone number.
        AppUser user = this.findByPhone(userResetPasswordSMS.getPhone());
        System.out.println("la variable User est " + user);
        System.out.println("la variable Phone est " + userResetPasswordSMS.getPhone());
        UserAccountResponse accountResponse = new UserAccountResponse();
        if (user != null) {
            String code = UserCode.getSmsCode();
            System.out.println("le code est" + code);
            this.smsService.SendSMS(userResetPasswordSMS.getPhone(),code);
            System.out.println("la variable User est" + user);
            user.setCode(code);
            appUserRepository.save(user);
            accountResponse.setResult(1);
        } else {
            accountResponse.setResult(0);
        }
        return accountResponse;
    }

    //Compare given code with the one stored in DB and reset password.
    public UserAccountResponse resetPasswordSMS(UserNewPasswordSMS userNewPasswordSMS) {
        AppUser user = this.findByPhone(userNewPasswordSMS.getPhone());
        UserAccountResponse accountResponse = new UserAccountResponse();
        if(user != null){
            if(user.getCode().equals(userNewPasswordSMS.getCode())){
                user.setPassword(passwordEncoder.encode(userNewPasswordSMS.getPassword()));
                user.setCode("expired");
                appUserRepository.save(user);
                accountResponse.setResult(1);
            } else {
                accountResponse.setResult(0);
            }
        } else {
            accountResponse.setResult(0);
        }
        return accountResponse;
    }


    private String buildEmail(String firstName, String lastName, String linkuser) {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" style=\"font-family:arial, 'helvetica neue', helvetica, sans-serif\">\n" +
                " <head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\n" +
                "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "  <meta content=\"telephone=no\" name=\"format-detection\">\n" +
                "  <title>New Template</title><!--[if (mso 16)]>\n" +
                "    <style type=\"text/css\">\n" +
                "    a {text-decoration: none;}\n" +
                "    </style>\n" +
                "    <![endif]--><!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--><!--[if gte mso 9]>\n" +
                "<xml>\n" +
                "    <o:OfficeDocumentSettings>\n" +
                "    <o:AllowPNG></o:AllowPNG>\n" +
                "    <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                "    </o:OfficeDocumentSettings>\n" +
                "</xml>\n" +
                "<![endif]--><!--[if !mso]><!-- -->\n" +
                "  <link href=\"https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&display=swap\" rel=\"stylesheet\">\n" +
                "  <link href=\"https://fonts.googleapis.com/css?family=Montserrat:500,800&display=swap&subset=cyrillic-ext\" rel=\"stylesheet\"><!--<![endif]-->\n" +
                "  <style type=\"text/css\">\n" +
                "#outlook a {\n" +
                "\tpadding:0;\n" +
                "}\n" +
                ".es-button {\n" +
                "\tmso-style-priority:100!important;\n" +
                "\ttext-decoration:none!important;\n" +
                "}\n" +
                "a[x-apple-data-detectors] {\n" +
                "\tcolor:inherit!important;\n" +
                "\ttext-decoration:none!important;\n" +
                "\tfont-size:inherit!important;\n" +
                "\tfont-family:inherit!important;\n" +
                "\tfont-weight:inherit!important;\n" +
                "\tline-height:inherit!important;\n" +
                "}\n" +
                ".es-desk-hidden {\n" +
                "\tdisplay:none;\n" +
                "\tfloat:left;\n" +
                "\toverflow:hidden;\n" +
                "\twidth:0;\n" +
                "\tmax-height:0;\n" +
                "\tline-height:0;\n" +
                "\tmso-hide:all;\n" +
                "}\n" +
                "@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120% } h1 { font-size:30px!important; text-align:center!important } h2 { font-size:24px!important; text-align:center!important } h3 { font-size:20px!important; text-align:center!important } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:30px!important; text-align:center!important } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:24px!important; text-align:center!important } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:20px!important; text-align:center!important } .es-menu td a { font-size:11px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:14px!important } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:14px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:12px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:inline-block!important } a.es-button, button.es-button { font-size:18px!important; display:inline-block!important } .es-adaptive table, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0!important } .es-m-p0r { padding-right:0!important } .es-m-p0l { padding-left:0!important } .es-m-p0t { padding-top:0!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } .es-desk-hidden { display:table-row!important; width:auto!important; overflow:visible!important; max-height:inherit!important } .es-m-p5 { padding:5px!important } .es-m-p5t { padding-top:5px!important } .es-m-p5b { padding-bottom:5px!important } .es-m-p5r { padding-right:5px!important } .es-m-p5l { padding-left:5px!important } .es-m-p10 { padding:10px!important } .es-m-p10t { padding-top:10px!important } .es-m-p10b { padding-bottom:10px!important } .es-m-p10r { padding-right:10px!important } .es-m-p10l { padding-left:10px!important } .es-m-p15 { padding:15px!important } .es-m-p15t { padding-top:15px!important } .es-m-p15b { padding-bottom:15px!important } .es-m-p15r { padding-right:15px!important } .es-m-p15l { padding-left:15px!important } .es-m-p20 { padding:20px!important } .es-m-p20t { padding-top:20px!important } .es-m-p20r { padding-right:20px!important } .es-m-p20l { padding-left:20px!important } .es-m-p25 { padding:25px!important } .es-m-p25t { padding-top:25px!important } .es-m-p25b { padding-bottom:25px!important } .es-m-p25r { padding-right:25px!important } .es-m-p25l { padding-left:25px!important } .es-m-p30 { padding:30px!important } .es-m-p30t { padding-top:30px!important } .es-m-p30b { padding-bottom:30px!important } .es-m-p30r { padding-right:30px!important } .es-m-p30l { padding-left:30px!important } .es-m-p35 { padding:35px!important } .es-m-p35t { padding-top:35px!important } .es-m-p35b { padding-bottom:35px!important } .es-m-p35r { padding-right:35px!important } .es-m-p35l { padding-left:35px!important } .es-m-p40 { padding:40px!important } .es-m-p40t { padding-top:40px!important } .es-m-p40b { padding-bottom:40px!important } .es-m-p40r { padding-right:40px!important } .es-m-p40l { padding-left:40px!important } }\n" +
                "</style>\n" +
                " </head>\n" +
                " <body style=\"width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\">\n" +
                "  <div class=\"es-wrapper-color\" style=\"background-color:#DDD4CC\"><!--[if gte mso 9]>\n" +
                "\t\t\t<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\n" +
                "\t\t\t\t<v:fill type=\"tile\" color=\"#ddd4cc\"></v:fill>\n" +
                "\t\t\t</v:background>\n" +
                "\t\t<![endif]-->\n" +
                "   <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#DDD4CC\">\n" +
                "     <tr>\n" +
                "      <td valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                "       <table class=\"es-header\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:#5F5449;background-repeat:repeat;background-position:center top\">\n" +
                "         <tr>\n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "           <table class=\"es-header-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#5F5449;width:600px\">\n" +
                "             <tr>\n" +
                "              <td align=\"left\" style=\"Margin:0;padding-left:20px;padding-right:20px;padding-top:30px;padding-bottom:30px\">\n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                 <tr>\n" +
                "                  <td class=\"es-m-p0r\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                     <tr>\n" +
                "                      <td align=\"left\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Montserrat, helvetica, arial, sans-serif;line-height:21px;color:#FCF3EA;font-size:14px\"><br></p></td>\n" +
                "                     </tr>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "           </table></td>\n" +
                "         </tr>\n" +
                "       </table>\n" +
                "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\n" +
                "         <tr>\n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#fcf3ea\" align=\"center\">\n" +
                "             <tr>\n" +
                "              <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                 <tr>\n" +
                "                  <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                     <tr>\n" +
                "                      <td align=\"center\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:42px;color:#5F5449;font-size:28px\"><strong>Registration confirmation</strong></p></td>\n" +
                "                     </tr>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "           </table></td>\n" +
                "         </tr>\n" +
                "       </table>\n" +
                "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\n" +
                "         <tr>\n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "           <table class=\"es-content-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FCF3EA;width:600px\">\n" +
                "             <tr>\n" +
                "              <td align=\"left\" style=\"padding:0;Margin:0\">\n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                 <tr>\n" +
                "                  <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:600px\">\n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                     <tr>\n" +
                "                      <td style=\"padding:0;Margin:0;font-size:0px\" align=\"center\"><img class=\"adapt-img\" src=\"https://ghflnc.stripocdn.email/content/guids/CABINET_82f408a3c9e392a0c63127abb841380321ad47f4515d155112360e3dc348e9d3/images/hf.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"600\"></td>\n" +
                "                     </tr>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td align=\"left\" style=\"Margin:0;padding-left:20px;padding-right:20px;padding-top:40px;padding-bottom:40px\">\n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                 <tr>\n" +
                "                  <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                     <tr>\n" +
                "                      <td align=\"left\" style=\"padding:0;Margin:0\"><h1 style=\"Margin:0;line-height:36px;mso-line-height-rule:exactly;font-family:Montserrat, 'Google Sans', 'Segoe UI', Roboto, Arial, Ubuntu, sans-serif;font-size:30px;font-style:normal;font-weight:normal;color:#5F5449\"><b>Complete the registration</b></h1></td>\n" +
                "                     </tr>\n" +
                "                     <tr>\n" +
                "                      <td align=\"left\" style=\"padding:0;Margin:0\"><h1 style=\"Margin:0;line-height:36px;mso-line-height-rule:exactly;font-family:Montserrat, 'Google Sans', 'Segoe UI', Roboto, Arial, Ubuntu, sans-serif;font-size:30px;font-style:normal;font-weight:normal;color:#5F5449\"><b>\"" + firstName + " " + lastName + "\"<br></b></h1></td>\n" +
                "                     </tr>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "                 <tr>\n" +
                "                  <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                     <tr>\n" +
                "                      <td align=\"left\" style=\"padding:0;Margin:0;padding-top:20px\"><!--[if mso]><a href=\" " + linkuser + "\" target=\"_blank\" hidden>\n" +
                "\t<v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" esdevVmlButton href=\" " + linkuser + "\" \n" +
                "                style=\"height:40px; v-text-anchor:middle; width:251px\" arcsize=\"0%\" stroke=\"f\"  fillcolor=\"#9b8269\">\n" +
                "\t\t<w:anchorlock></w:anchorlock>\n" +
                "\t\t<center style='color:#fcf3ea; font-family:Montserrat, \"Google Sans\", \"Segoe UI\", Roboto, Arial, Ubuntu, sans-serif; font-size:14px; font-weight:400; line-height:14px;  mso-text-raise:1px'>CONFIRM YOUR EMAIL</center>\n" +
                "\t</v:roundrect></a>\n" +
                "<![endif]--><!--[if !mso]><!-- --><span class=\"msohide es-button-border\" style=\"border-style:solid;border-color:#2CB543;background:#9B8269;border-width:0px;display:inline-block;border-radius:0px;width:auto;mso-hide:all\"><a href=\" " + linkuser + "\" class=\"es-button\" target=\"\" style=\"mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;color:#FCF3EA;font-size:18px;display:inline-block;background:#9B8269;border-radius:0px;font-family:Montserrat, 'Google Sans', 'Segoe UI', Roboto, Arial, Ubuntu, sans-serif;font-weight:normal;font-style:normal;line-height:22px;width:auto;text-align:center;padding:10px 20px 10px 20px\">CONFIRM YOUR EMAIL</a></span><!--<![endif]--></td>\n" +
                "                     </tr>\n" +
                "                     <tr>\n" +
                "                      <td align=\"left\" style=\"padding:0;Margin:0;padding-top:20px;padding-bottom:20px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Montserrat, helvetica, arial, sans-serif;line-height:21px;color:#5F5449;font-size:14px\">If you didn't create an account, please ignore this message.</p></td>\n" +
                "                     </tr>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "           </table></td>\n" +
                "         </tr>\n" +
                "       </table>\n" +
                "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\n" +
                "         <tr>\n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#FFFFFF\" align=\"center\">\n" +
                "             <tr>\n" +
                "              <td align=\"left\" style=\"Margin:0;padding-left:20px;padding-right:20px;padding-top:40px;padding-bottom:40px\">\n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                 <tr>\n" +
                "                  <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
                "                   <table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;border-left:2px solid #dad3d3;border-right:2px solid #dad3d3;border-top:2px solid #dad3d3;border-bottom:2px solid #dad3d3\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\">\n" +
                "                     <tr>\n" +
                "                      <td class=\"es-m-p20r es-m-p20l\" align=\"center\" style=\"padding:0;Margin:0\"><h2 style=\"Margin:0;line-height:29px;mso-line-height-rule:exactly;font-family:Montserrat, 'Google Sans', 'Segoe UI', Roboto, Arial, Ubuntu, sans-serif;font-size:24px;font-style:normal;font-weight:normal;color:#5F5449\" class=\"m-fs-20\">Need any help?</h2></td>\n" +
                "                     </tr>\n" +
                "                     <tr>\n" +
                "                      <td class=\"es-m-p20r es-m-p20l\" align=\"center\" style=\"padding:0;Margin:0;padding-top:20px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Montserrat, helvetica, arial, sans-serif;line-height:21px;color:#ad940d;font-size:14px\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#5F5449;font-size:14px\">Visit Help Center</a></p></td>\n" +
                "                     </tr>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "           </table></td>\n" +
                "         </tr>\n" +
                "       </table>\n" +
                "       <table class=\"es-footer\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:#5F5449;background-repeat:repeat;background-position:center top\">\n" +
                "         <tr>\n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "           <table class=\"es-footer-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#5F5449;width:600px\">\n" +
                "             <tr>\n" +
                "              <td align=\"left\" style=\"Margin:0;padding-bottom:20px;padding-left:20px;padding-right:20px;padding-top:40px\">\n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                 <tr>\n" +
                "                  <td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                     <tr>\n" +
                "                      <td style=\"padding:0;Margin:0;font-size:0\" align=\"center\">\n" +
                "                       <table class=\"es-table-not-adapt es-social\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                         <tr>\n" +
                "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:30px\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#FCF3EA;font-size:14px\"><img title=\"Facebook\" src=\"https://ghflnc.stripocdn.email/content/assets/img/social-icons/square-white/facebook-square-white.png\" alt=\"Fb\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\n" +
                "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:30px\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#FCF3EA;font-size:14px\"><img title=\"Twitter\" src=\"https://ghflnc.stripocdn.email/content/assets/img/social-icons/square-white/twitter-square-white.png\" alt=\"Tw\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\n" +
                "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:30px\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#FCF3EA;font-size:14px\"><img title=\"Instagram\" src=\"https://ghflnc.stripocdn.email/content/assets/img/social-icons/square-white/instagram-square-white.png\" alt=\"Inst\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\n" +
                "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#FCF3EA;font-size:14px\"><img title=\"Youtube\" src=\"https://ghflnc.stripocdn.email/content/assets/img/social-icons/square-white/youtube-square-white.png\" alt=\"Yt\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\n" +
                "                         </tr>\n" +
                "                       </table></td>\n" +
                "                     </tr>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "           </table></td>\n" +
                "         </tr>\n" +
                "       </table>\n" +
                "       <table class=\"es-footer\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:#5F5449;background-repeat:repeat;background-position:center top\">\n" +
                "         <tr>\n" +
                "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "           <table class=\"es-footer-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#5F5449;width:600px\">\n" +
                "             <tr>\n" +
                "              <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                 <tr>\n" +
                "                  <td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                     <tr>\n" +
                "                      <td style=\"padding:0;Margin:0\">\n" +
                "                       <table class=\"es-menu\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                         <tr class=\"links\">\n" +
                "                          <td valign=\"top\" align=\"right\" style=\"Margin:0;padding-top:10px;padding-bottom:10px;padding-left:15px;padding-right:15px;border:0\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;display:block;font-family:Montserrat, helvetica, arial, sans-serif;color:#FCF3EA;font-size:12px;font-weight:normal\">PRIVACY POLICY</a></td>\n" +
                "                          <td valign=\"top\" align=\"left\" style=\"Margin:0;padding-top:10px;padding-bottom:10px;padding-left:15px;padding-right:15px;border:0\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;display:block;font-family:Montserrat, helvetica, arial, sans-serif;color:#FCF3EA;font-size:12px;font-weight:normal\">TERMS OF USE</a></td>\n" +
                "                         </tr>\n" +
                "                       </table></td>\n" +
                "                     </tr>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "             <tr>\n" +
                "              <td align=\"left\" style=\"Margin:0;padding-top:20px;padding-bottom:20px;padding-left:20px;padding-right:20px\">\n" +
                "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                 <tr>\n" +
                "                  <td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\n" +
                "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                     <tr>\n" +
                "                      <td style=\"padding:0;Margin:0;display:none\" align=\"center\"></td>\n" +
                "                     </tr>\n" +
                "                   </table></td>\n" +
                "                 </tr>\n" +
                "               </table></td>\n" +
                "             </tr>\n" +
                "           </table></td>\n" +
                "         </tr>\n" +
                "       </table></td>\n" +
                "     </tr>\n" +
                "   </table>\n" +
                "  </div>\n" +
                " </body>\n" +
                "</html>";
    }

    private String buildEmailadmin(String firstName, String lastName, Integer cin, String phonenumber, String linkadmin) {
    return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" style=\"font-family:arial, 'helvetica neue', helvetica, sans-serif\">\n" +
            " <head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\n" +
            "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
            "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
            "  <meta content=\"telephone=no\" name=\"format-detection\">\n" +
            "  <title>New Template</title><!--[if (mso 16)]>\n" +
            "    <style type=\"text/css\">\n" +
            "    a {text-decoration: none;}\n" +
            "    </style>\n" +
            "    <![endif]--><!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--><!--[if gte mso 9]>\n" +
            "<xml>\n" +
            "    <o:OfficeDocumentSettings>\n" +
            "    <o:AllowPNG></o:AllowPNG>\n" +
            "    <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
            "    </o:OfficeDocumentSettings>\n" +
            "</xml>\n" +
            "<![endif]--><!--[if !mso]><!-- -->\n" +
            "  <link href=\"https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&display=swap\" rel=\"stylesheet\">\n" +
            "  <link href=\"https://fonts.googleapis.com/css?family=Montserrat:500,800&display=swap&subset=cyrillic-ext\" rel=\"stylesheet\"><!--<![endif]-->\n" +
            "  <style type=\"text/css\">\n" +
            "#outlook a {\n" +
            "\tpadding:0;\n" +
            "}\n" +
            ".es-button {\n" +
            "\tmso-style-priority:100!important;\n" +
            "\ttext-decoration:none!important;\n" +
            "}\n" +
            "a[x-apple-data-detectors] {\n" +
            "\tcolor:inherit!important;\n" +
            "\ttext-decoration:none!important;\n" +
            "\tfont-size:inherit!important;\n" +
            "\tfont-family:inherit!important;\n" +
            "\tfont-weight:inherit!important;\n" +
            "\tline-height:inherit!important;\n" +
            "}\n" +
            ".es-desk-hidden {\n" +
            "\tdisplay:none;\n" +
            "\tfloat:left;\n" +
            "\toverflow:hidden;\n" +
            "\twidth:0;\n" +
            "\tmax-height:0;\n" +
            "\tline-height:0;\n" +
            "\tmso-hide:all;\n" +
            "}\n" +
            "@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120% } h1 { font-size:30px!important; text-align:center!important } h2 { font-size:24px!important; text-align:center!important } h3 { font-size:20px!important; text-align:center!important } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:30px!important; text-align:center!important } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:24px!important; text-align:center!important } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:20px!important; text-align:center!important } .es-menu td a { font-size:11px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:14px!important } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:14px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:12px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:inline-block!important } a.es-button, button.es-button { font-size:18px!important; display:inline-block!important } .es-adaptive table, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0!important } .es-m-p0r { padding-right:0!important } .es-m-p0l { padding-left:0!important } .es-m-p0t { padding-top:0!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } .es-desk-hidden { display:table-row!important; width:auto!important; overflow:visible!important; max-height:inherit!important } .es-m-p5 { padding:5px!important } .es-m-p5t { padding-top:5px!important } .es-m-p5b { padding-bottom:5px!important } .es-m-p5r { padding-right:5px!important } .es-m-p5l { padding-left:5px!important } .es-m-p10 { padding:10px!important } .es-m-p10t { padding-top:10px!important } .es-m-p10b { padding-bottom:10px!important } .es-m-p10r { padding-right:10px!important } .es-m-p10l { padding-left:10px!important } .es-m-p15 { padding:15px!important } .es-m-p15t { padding-top:15px!important } .es-m-p15b { padding-bottom:15px!important } .es-m-p15r { padding-right:15px!important } .es-m-p15l { padding-left:15px!important } .es-m-p20 { padding:20px!important } .es-m-p20t { padding-top:20px!important } .es-m-p20r { padding-right:20px!important } .es-m-p20l { padding-left:20px!important } .es-m-p25 { padding:25px!important } .es-m-p25t { padding-top:25px!important } .es-m-p25b { padding-bottom:25px!important } .es-m-p25r { padding-right:25px!important } .es-m-p25l { padding-left:25px!important } .es-m-p30 { padding:30px!important } .es-m-p30t { padding-top:30px!important } .es-m-p30b { padding-bottom:30px!important } .es-m-p30r { padding-right:30px!important } .es-m-p30l { padding-left:30px!important } .es-m-p35 { padding:35px!important } .es-m-p35t { padding-top:35px!important } .es-m-p35b { padding-bottom:35px!important } .es-m-p35r { padding-right:35px!important } .es-m-p35l { padding-left:35px!important } .es-m-p40 { padding:40px!important } .es-m-p40t { padding-top:40px!important } .es-m-p40b { padding-bottom:40px!important } .es-m-p40r { padding-right:40px!important } .es-m-p40l { padding-left:40px!important } }\n" +
            "</style>\n" +
            " </head>\n" +
            " <body style=\"width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\">\n" +
            "  <div class=\"es-wrapper-color\" style=\"background-color:#DDD4CC\"><!--[if gte mso 9]>\n" +
            "\t\t\t<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\n" +
            "\t\t\t\t<v:fill type=\"tile\" color=\"#ddd4cc\"></v:fill>\n" +
            "\t\t\t</v:background>\n" +
            "\t\t<![endif]-->\n" +
            "   <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#DDD4CC\">\n" +
            "     <tr>\n" +
            "      <td valign=\"top\" style=\"padding:0;Margin:0\">\n" +
            "       <table class=\"es-header\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:#5F5449;background-repeat:repeat;background-position:center top\">\n" +
            "         <tr>\n" +
            "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
            "           <table class=\"es-header-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#5F5449;width:600px\">\n" +
            "             <tr>\n" +
            "              <td align=\"left\" style=\"Margin:0;padding-left:20px;padding-right:20px;padding-top:30px;padding-bottom:30px\">\n" +
            "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                 <tr>\n" +
            "                  <td class=\"es-m-p0r\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
            "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                     <tr>\n" +
            "                      <td align=\"left\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Montserrat, helvetica, arial, sans-serif;line-height:21px;color:#FCF3EA;font-size:14px\"><br></p></td>\n" +
            "                     </tr>\n" +
            "                   </table></td>\n" +
            "                 </tr>\n" +
            "               </table></td>\n" +
            "             </tr>\n" +
            "           </table></td>\n" +
            "         </tr>\n" +
            "       </table>\n" +
            "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\n" +
            "         <tr>\n" +
            "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
            "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#fcf3ea\" align=\"center\">\n" +
            "             <tr>\n" +
            "              <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
            "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                 <tr>\n" +
            "                  <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
            "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                     <tr>\n" +
            "                      <td align=\"center\" style=\"padding:0;Margin:0\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:helvetica, 'helvetica neue', arial, verdana, sans-serif;line-height:42px;color:#5F5449;font-size:28px\"><strong>Registration validation<br></strong></p></td>\n" +
            "                     </tr>\n" +
            "                   </table></td>\n" +
            "                 </tr>\n" +
            "               </table></td>\n" +
            "             </tr>\n" +
            "           </table></td>\n" +
            "         </tr>\n" +
            "       </table>\n" +
            "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\n" +
            "         <tr>\n" +
            "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
            "           <table class=\"es-content-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FCF3EA;width:600px\">\n" +
            "             <tr>\n" +
            "              <td align=\"left\" style=\"padding:0;Margin:0\">\n" +
            "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                 <tr>\n" +
            "                  <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:600px\">\n" +
            "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                     <tr>\n" +
            "                      <td style=\"padding:0;Margin:0;font-size:0px\" align=\"center\"><img class=\"adapt-img\" src=\"https://ghflnc.stripocdn.email/content/guids/CABINET_82f408a3c9e392a0c63127abb841380321ad47f4515d155112360e3dc348e9d3/images/hf.png\" alt style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\" width=\"600\"></td>\n" +
            "                     </tr>\n" +
            "                   </table></td>\n" +
            "                 </tr>\n" +
            "               </table></td>\n" +
            "             </tr>\n" +
            "             <tr>\n" +
            "              <td align=\"left\" style=\"padding:0;Margin:0;padding-left:35px;padding-top:40px;padding-bottom:40px\"><!--[if mso]><table style=\"width:565px\" cellpadding=\"0\" cellspacing=\"0\"><tr><td style=\"width:501px\" valign=\"top\"><![endif]-->\n" +
            "               <table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\">\n" +
            "                 <tr>\n" +
            "                  <td class=\"es-m-p20b\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:501px\">\n" +
            "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                     <tr>\n" +
            "                      <td align=\"left\" style=\"padding:0;Margin:0\"><h1 style=\"Margin:0;line-height:36px;mso-line-height-rule:exactly;font-family:Montserrat, 'Google Sans', 'Segoe UI', Roboto, Arial, Ubuntu, sans-serif;font-size:30px;font-style:normal;font-weight:normal;color:#5F5449\"><b>Validate the registration</b><br></h1></td>\n" +
            "                     </tr>\n" +
            "                     <tr>\n" +
            "                      <td align=\"left\" style=\"padding:0;Margin:0\"><h1 style=\"Margin:0;line-height:36px;mso-line-height-rule:exactly;font-family:Montserrat, 'Google Sans', 'Segoe UI', Roboto, Arial, Ubuntu, sans-serif;font-size:30px;font-style:normal;font-weight:normal;color:#5F5449\"><b>\" "+ firstName +   lastName + "\"</b></h1></td>\n" +
            "                     </tr>\n" +
            "                     <tr>\n" +
            "                      <td align=\"left\" style=\"padding:0;Margin:0\"><h1 style=\"Margin:0;line-height:36px;mso-line-height-rule:exactly;font-family:Montserrat, 'Google Sans', 'Segoe UI', Roboto, Arial, Ubuntu, sans-serif;font-size:30px;font-style:normal;font-weight:normal;color:#5F5449\"><b>With the national ID card of "+cin+"</b></h1></td>\n" +
            "                     </tr>\n" +
            "                     <tr>\n" +
            "                      <td align=\"left\" style=\"padding:0;Margin:0\"><h1 style=\"Margin:0;line-height:36px;mso-line-height-rule:exactly;font-family:Montserrat, 'Google Sans', 'Segoe UI', Roboto, Arial, Ubuntu, sans-serif;font-size:30px;font-style:normal;font-weight:normal;color:#5F5449\"><b>And phone number "+phonenumber+"</b></h1></td>\n" +
            "                     </tr>\n" +
            "                   </table></td>\n" +
            "                 </tr>\n" +
            "                 <tr>\n" +
            "                  <td class=\"es-m-p20b\" valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:503px\">\n" +
            "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                     <tr>\n" +
            "                      <td align=\"left\" style=\"padding:0;Margin:0;padding-top:20px\"><!--[if mso]><a href=\"linkadmin\" target=\"_blank\" hidden>\n" +
            "\t<v:roundrect xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"urn:schemas-microsoft-com:office:word\" esdevVmlButton href=\"linkadmin\" \n" +
            "                style=\"height:41px; v-text-anchor:middle; width:305px\" arcsize=\"0%\" stroke=\"f\"  fillcolor=\"#9b8269\">\n" +
            "\t\t<w:anchorlock></w:anchorlock>\n" +
            "\t\t<center style='color:#fcf3ea; font-family:Montserrat, \"Google Sans\", \"Segoe UI\", Roboto, Arial, Ubuntu, sans-serif; font-size:15px; font-weight:400; line-height:15px;  mso-text-raise:1px'>VALIDATE THE USER'S EMAIL</center>\n" +
            "\t</v:roundrect></a>\n" +
            "<![endif]--><!--[if !mso]><!-- --><span class=\"msohide es-button-border\" style=\"border-style:solid;border-color:#2CB543;background:#9B8269;border-width:0px;display:inline-block;border-radius:0px;width:auto;mso-hide:all\"><a href=\"" +linkadmin+"\" class=\"es-button\" target=\"_blank\" style=\"mso-style-priority:100 !important;text-decoration:none;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;color:#FCF3EA;font-size:18px;display:inline-block;background:#9B8269;border-radius:0px;font-family:Montserrat, 'Google Sans', 'Segoe UI', Roboto, Arial, Ubuntu, sans-serif;font-weight:normal;font-style:normal;line-height:22px;width:auto;text-align:center;padding:10px 20px 10px 20px\">VALIDATE THE USER'S EMAIL </a></span><!--<![endif]--></td>\n" +
            "                     </tr>\n" +
            "                   </table></td>\n" +
            "                 </tr>\n" +
            "               </table><!--[if mso]></td><td style=\"width:20px\"></td><td style=\"width:44px\" valign=\"top\"><![endif]-->\n" +
            "               <table class=\"es-right\" cellspacing=\"0\" cellpadding=\"0\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\">\n" +
            "                 <tr>\n" +
            "                  <td align=\"left\" style=\"padding:0;Margin:0;width:44px\">\n" +
            "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                     <tr>\n" +
            "                     </tr>\n" +
            "                   </table></td>\n" +
            "                 </tr>\n" +
            "               </table><!--[if mso]></td></tr></table><![endif]--></td>\n" +
            "             </tr>\n" +
            "           </table></td>\n" +
            "         </tr>\n" +
            "       </table>\n" +
            "       <table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\">\n" +
            "         <tr>\n" +
            "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
            "           <table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#FFFFFF\" align=\"center\">\n" +
            "             <tr>\n" +
            "              <td align=\"left\" style=\"Margin:0;padding-left:20px;padding-right:20px;padding-top:40px;padding-bottom:40px\">\n" +
            "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                 <tr>\n" +
            "                  <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\">\n" +
            "                   <table style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;border-left:2px solid #dad3d3;border-right:2px solid #dad3d3;border-top:2px solid #dad3d3;border-bottom:2px solid #dad3d3\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\">\n" +
            "                     <tr>\n" +
            "                      <td class=\"es-m-p20r es-m-p20l\" align=\"center\" style=\"padding:0;Margin:0\"><h2 style=\"Margin:0;line-height:29px;mso-line-height-rule:exactly;font-family:Montserrat, 'Google Sans', 'Segoe UI', Roboto, Arial, Ubuntu, sans-serif;font-size:24px;font-style:normal;font-weight:normal;color:#5F5449\" class=\"m-fs-20\">Need any help?</h2></td>\n" +
            "                     </tr>\n" +
            "                     <tr>\n" +
            "                      <td class=\"es-m-p20r es-m-p20l\" align=\"center\" style=\"padding:0;Margin:0;padding-top:20px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:Montserrat, helvetica, arial, sans-serif;line-height:21px;color:#ad940d;font-size:14px\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#5F5449;font-size:14px\">Visit Help Center</a></p></td>\n" +
            "                     </tr>\n" +
            "                   </table></td>\n" +
            "                 </tr>\n" +
            "               </table></td>\n" +
            "             </tr>\n" +
            "           </table></td>\n" +
            "         </tr>\n" +
            "       </table>\n" +
            "       <table class=\"es-footer\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:#5F5449;background-repeat:repeat;background-position:center top\">\n" +
            "         <tr>\n" +
            "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
            "           <table class=\"es-footer-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#5F5449;width:600px\">\n" +
            "             <tr>\n" +
            "              <td align=\"left\" style=\"Margin:0;padding-bottom:20px;padding-left:20px;padding-right:20px;padding-top:40px\">\n" +
            "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                 <tr>\n" +
            "                  <td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\n" +
            "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                     <tr>\n" +
            "                      <td style=\"padding:0;Margin:0;font-size:0\" align=\"center\">\n" +
            "                       <table class=\"es-table-not-adapt es-social\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                         <tr>\n" +
            "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:30px\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#FCF3EA;font-size:14px\"><img title=\"Facebook\" src=\"https://ghflnc.stripocdn.email/content/assets/img/social-icons/square-white/facebook-square-white.png\" alt=\"Fb\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\n" +
            "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:30px\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#FCF3EA;font-size:14px\"><img title=\"Twitter\" src=\"https://ghflnc.stripocdn.email/content/assets/img/social-icons/square-white/twitter-square-white.png\" alt=\"Tw\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\n" +
            "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:30px\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#FCF3EA;font-size:14px\"><img title=\"Instagram\" src=\"https://ghflnc.stripocdn.email/content/assets/img/social-icons/square-white/instagram-square-white.png\" alt=\"Inst\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\n" +
            "                          <td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#FCF3EA;font-size:14px\"><img title=\"Youtube\" src=\"https://ghflnc.stripocdn.email/content/assets/img/social-icons/square-white/youtube-square-white.png\" alt=\"Yt\" height=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\n" +
            "                         </tr>\n" +
            "                       </table></td>\n" +
            "                     </tr>\n" +
            "                   </table></td>\n" +
            "                 </tr>\n" +
            "               </table></td>\n" +
            "             </tr>\n" +
            "           </table></td>\n" +
            "         </tr>\n" +
            "       </table>\n" +
            "       <table class=\"es-footer\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:#5F5449;background-repeat:repeat;background-position:center top\">\n" +
            "         <tr>\n" +
            "          <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
            "           <table class=\"es-footer-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#5F5449;width:600px\">\n" +
            "             <tr>\n" +
            "              <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
            "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                 <tr>\n" +
            "                  <td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\n" +
            "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                     <tr>\n" +
            "                      <td style=\"padding:0;Margin:0\">\n" +
            "                       <table class=\"es-menu\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                         <tr class=\"links\">\n" +
            "                          <td valign=\"top\" align=\"right\" style=\"Margin:0;padding-top:10px;padding-bottom:10px;padding-left:15px;padding-right:15px;border:0\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;display:block;font-family:Montserrat, helvetica, arial, sans-serif;color:#FCF3EA;font-size:12px;font-weight:normal\">PRIVACY POLICY</a></td>\n" +
            "                          <td valign=\"top\" align=\"left\" style=\"Margin:0;padding-top:10px;padding-bottom:10px;padding-left:15px;padding-right:15px;border:0\"><a target=\"_blank\" href=\"https://viewstripo.email\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;display:block;font-family:Montserrat, helvetica, arial, sans-serif;color:#FCF3EA;font-size:12px;font-weight:normal\">TERMS OF USE</a></td>\n" +
            "                         </tr>\n" +
            "                       </table></td>\n" +
            "                     </tr>\n" +
            "                   </table></td>\n" +
            "                 </tr>\n" +
            "               </table></td>\n" +
            "             </tr>\n" +
            "             <tr>\n" +
            "              <td align=\"left\" style=\"Margin:0;padding-top:20px;padding-bottom:20px;padding-left:20px;padding-right:20px\">\n" +
            "               <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                 <tr>\n" +
            "                  <td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\n" +
            "                   <table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
            "                     <tr>\n" +
            "                      <td style=\"padding:0;Margin:0;display:none\" align=\"center\"></td>\n" +
            "                     </tr>\n" +
            "                   </table></td>\n" +
            "                 </tr>\n" +
            "               </table></td>\n" +
            "             </tr>\n" +
            "           </table></td>\n" +
            "         </tr>\n" +
            "       </table></td>\n" +
            "     </tr>\n" +
            "   </table>\n" +
            "  </div>\n" +
            " </body>\n" +
            "</html>";
    }






    public AppUser getUserByID(Long idUser){
        return appUserRepository.getById(idUser);
    }

}





