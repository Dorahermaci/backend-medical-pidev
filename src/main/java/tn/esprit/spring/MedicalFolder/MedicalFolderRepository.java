package tn.esprit.spring.MedicalFolder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.AppUser.AppUser;

import java.util.List;

@Repository
public interface MedicalFolderRepository extends JpaRepository<MedicalFolder, Long> {

    List<MedicalFolder> findByPatient(AppUser patient);

    List<MedicalFolder> findByDoctor(AppUser doctor);

}