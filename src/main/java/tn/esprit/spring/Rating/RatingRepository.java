package tn.esprit.spring.Rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating,Long> {
   // List<Rating> findByTrainingCourse(TrainingCourse trainingCourse);
}
