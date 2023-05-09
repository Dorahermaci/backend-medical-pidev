package tn.esprit.spring.TrainingCourse;

import com.google.zxing.common.BitMatrix;

import java.util.List;
import java.util.Optional;

public interface ITrainingCourseService {


    TrainingCourse addTrainingCourse(TrainingCourse trainingCourse);

    TrainingCourse updateTrainingCourse(TrainingCourse trainingCourse);

    void deleteTrainingCourse(Long idCourse);

    List<TrainingCourse> getAllTrainingCourses();

    abstract Optional<TrainingCourse> getTrainingCourseById(Long id);


    TrainingCourse getTrainingCourseById1(Long id);

    void assignTrainer(Long trainingCourseId, Long trainerId);

    TrainingCourse addLikeToCourse(Long courseId);

    TrainingCourse addDislikeToCourse(Long courseId);

    List<TrainingCourse> findAllOrderByAverageRatingDesc();

    String generateMeetingLink();

    void openMeetingLink();

    BitMatrix generateQRCode(TrainingCourse entity, int width, int height) throws Exception;




}
