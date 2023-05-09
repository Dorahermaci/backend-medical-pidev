package tn.esprit.spring.Register;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.TrainingCourse.TrainingCourse;


import java.util.List;

@Repository
public interface RegisterRepository extends JpaRepository<Register,Long> {

    List<Register> findByTrainingCourseId(Long idCourse);


    List<Register> findByAppUserId(Long idAppUser);

    List<Register> findByCompletionStatus(boolean completionStatus);


    @Query("SELECT COUNT(r) FROM Register r WHERE r.trainingCourse.id = :courseId")
    int countByTrainingCourseId(@Param("courseId") long courseId);

    @Query("SELECT COUNT(r) FROM Register r WHERE r.trainingCourse.id = :courseId AND r.completionStatus = true")
    int countSucceededUsersInCourse(@Param("courseId") long courseId);

    @Query("SELECT COUNT(r) FROM Register r WHERE r.trainingCourse.id = :courseId AND r.completionStatus = false")
    int countFailedUsersInCourse(@Param("courseId") long courseId);

    Register findByAppUserAndTrainingCourse(AppUser appUser, TrainingCourse trainingCourse);

    Register findByAppUser(Long appUserId);
    List<Register> findByAppUserIdAndCompletionStatusTrue(Long appUserId);



}
