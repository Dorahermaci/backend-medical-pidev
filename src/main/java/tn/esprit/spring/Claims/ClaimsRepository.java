package tn.esprit.spring.Claims;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserRole;

import java.util.List;

@Repository
public interface ClaimsRepository extends JpaRepository<Claims, Long> {
    List<Claims> findByAppUserAndAppUserAppUserRole(AppUser appUser, AppUserRole appUserRole);
    int countByStatus(Claims.ClaimStatus status);


}