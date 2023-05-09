package tn.esprit.spring.TrainingCourse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingCourseRepository extends JpaRepository<TrainingCourse,Long> {


    TrainingCourse getTrainingcourseByid(long courseId);

    @Query("SELECT t FROM TrainingCourse t LEFT JOIN t.ratings r GROUP BY t.id ORDER BY AVG(r.note) DESC")
    List<TrainingCourse> findAllOrderByAvgRatingDesc();


}
