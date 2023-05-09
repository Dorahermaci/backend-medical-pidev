package tn.esprit.spring.Certificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate,Long> {
    @Query("SELECT (r) FROM Certificate r WHERE r.idCertificate = :idCertificate ")
    Certificate findByCertificateId(Long idCertificate);
}
