package tn.esprit.spring.Rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.TrainingCourse.ITrainingCourseService;


import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/Rating")
public class RatingController {

    @Autowired
    private IRatingService ratingService;
    @Autowired
    private ITrainingCourseService trainingCourseService;

    @PostMapping("createRating/{courseId}/")
    public ResponseEntity<Object> createRating(@RequestBody @Valid Rating rating, @PathVariable Long courseId) {
        if (ratingService.hasBadWord(rating.getComment())) {
            return ResponseEntity.badRequest().body("The comment has bad words. Please remove them and try again.");
        }
        Rating createdRating = ratingService.createRating(rating, courseId);
        return ResponseEntity.ok().body(createdRating);
    }


        @PostMapping("calculateAverageRating/{courseId}/calculate-average-rating")
        public ResponseEntity<String> calculateAverageRating (@PathVariable Long courseId){
            ratingService.calculateAndSetAverageRating(courseId);
            return ResponseEntity.ok("Average rating calculated and updated for course with id: " + courseId);
        }
    }
