package tn.esprit.spring.Registration.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AdminConfirmationTokenRepository  extends JpaRepository<AdminConfirmationToken, Long> {

    Optional<AdminConfirmationToken> findByAdmintoken(String admintoken);

    @Transactional
    @Modifying
    @Query("UPDATE AdminConfirmationToken c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.admintoken = ?1")
    int updateConfirmedAt(String admintoken,
                          LocalDateTime confirmedAt);
}
