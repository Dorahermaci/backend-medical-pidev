package tn.esprit.spring.Trainer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainersRepository extends JpaRepository<Trainers,Long> {



}
