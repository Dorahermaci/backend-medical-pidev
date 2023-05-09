package tn.esprit.spring.Career;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerRepository extends JpaRepository<Career,Long> {


    List<Career> findByAppUserId(Long idAppUser);


    List<Career> findByTrainerId(Long idTrainer);
}
