package tn.esprit.spring.Rating;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import tn.esprit.spring.Exception.ResourceNotFoundException;
import tn.esprit.spring.TrainingCourse.TrainingCourse;
import tn.esprit.spring.TrainingCourse.TrainingCourseRepository;


import java.util.List;

@Service
@Slf4j
@Validated
public class RatingService implements IRatingService {
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    private TrainingCourseRepository trainingCourseRepository;


    @Override
    public Rating createRating(Rating rating, Long courseId) {
        // Validate the rating note
        if (rating.getNote() == null || rating.getNote() < 0 || rating.getNote() > 10) {
            throw new IllegalArgumentException("Note must be between 0 and 10");
        }
        if (hasBadWord(rating.getComment())) {
            throw new IllegalArgumentException("Comment contains bad word");
        }

        TrainingCourse course = trainingCourseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // Calculate and set average rating
        calculateAndSetAverageRating(courseId);

        rating.setTrainingCourse(course);
        return ratingRepository.save(rating);

    }
    @Override
    public void calculateAndSetAverageRating(Long courseId) {
        TrainingCourse course = trainingCourseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        List<Rating> ratings = course.getRatings();

        if (ratings.isEmpty()) {
            course.setAvgRate(0);
            return;
        }

        int totalRating = 0;
        for (Rating rating : ratings) {
            totalRating += rating.getNote();
        }
        float averageRating = totalRating / ratings.size();

        course.setAvgRate(averageRating);
        trainingCourseRepository.save(course);
    }

    private final String[] badWords = {"fuck", "bitch", "shit"};

    @Override
    public boolean hasBadWord(String comment) {
        for (String word : badWords) {
            if (comment.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

}