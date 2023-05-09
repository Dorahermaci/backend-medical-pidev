package tn.esprit.spring.Registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.EntityResponse;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserRepository;
import tn.esprit.spring.AppUser.JwtRequest;
import tn.esprit.spring.AppUser.JwtResponse;
import tn.esprit.spring.util.JwtUtil;

import java.util.HashSet;
import java.util.Set;

@Service
public class JwtService implements UserDetailsService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppUserRepository userDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String createJwtToken(JwtRequest jwtRequest) throws Exception {
        String email = jwtRequest.getUserName();
        String userPassword = jwtRequest.getUserPassword();
        authenticate(email, userPassword);
        UserDetails userDetails = loadUserByUsername(email);
        String newGeneratedToken = jwtUtil.generateToken(userDetails);
        AppUser user = userDao.findByEmail(email).get();
        if (user != null)
        {
            if (user.getEnabled() && !user.getLocked()) {
                 JwtResponse jwtResponse = new JwtResponse(user, newGeneratedToken);
                return (user.getFirstName() + " " + user.getLastName() + " "  +" Connected \n"   + "With the Jwt token : " + " " + jwtResponse.getJwtToken() );
            } else return ("Please verify your account first and make sure the admin validate it");
        }
        else return ("Password or username incorrect");

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userDao.findByEmail(email).get();

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    getAuthority(user)
            );
        } else {
            throw new UsernameNotFoundException("User not found with username: " + email);
        }
    }

    private Set getAuthority(AppUser user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getAppUserRole()));
        return authorities;
    }

    private ResponseEntity<String> authenticate(String userName, String userPassword) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, userPassword));
        } catch (DisabledException e) {
            return ResponseEntity.ok("User Disabled");
        } catch (BadCredentialsException e) {
            return ResponseEntity.ok("Invalid Password or Email ");
        }
        return null;
    }

}
