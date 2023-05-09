package tn.esprit.spring.AppUser;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);

    public Optional<AppUser> findByPhonenumber(String phone);



    @Transactional
    @Modifying
    @Query("UPDATE AppUser a " +
            "SET a.locked = FALSE WHERE a.email = ?1")
    int enableAppUser(String email);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a " +
            "SET a.enabled = TRUE WHERE a.email = ?1")
    int enableFullAppUser(String email);

    Optional<AppUser> findByIdAndAppUserRole(Long patientId, AppUserRole patient);


    long countByAppUserRole(AppUserRole appUserRole);


    @Query("SELECT u FROM AppUser u WHERE u.appUserRole = :role")
    List<AppUser> findByRole(@Param("role") AppUserRole role);


}
