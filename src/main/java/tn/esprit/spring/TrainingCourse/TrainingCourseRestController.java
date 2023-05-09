package tn.esprit.spring.TrainingCourse;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/TrainingCourse")
public class TrainingCourseRestController {
    @Autowired
    private ITrainingCourseService trainingCourseService;

    @PostMapping("addTrainingCourse")
    public TrainingCourse addTrainingCourse(@RequestBody TrainingCourse trainingCourse) {
        return trainingCourseService.addTrainingCourse(trainingCourse);
    }

    @PutMapping("updateTrainingCourse")
    public TrainingCourse updateTrainingCourse(@RequestBody TrainingCourse trainingCourse) {
        return trainingCourseService.updateTrainingCourse(trainingCourse);
    }

    @DeleteMapping("deleteTrainingCourse/{idCourse}")
    public void deleteTrainingCourse(@PathVariable Long idCourse) {
        trainingCourseService.deleteTrainingCourse(idCourse);
    }

    @GetMapping("getAllTrainingCourses/")
    public List<TrainingCourse> getAllTrainingCourses() {
        return trainingCourseService.getAllTrainingCourses();
    }

    @GetMapping("getTrainingCourseById/{idCourse}")
    public Optional<TrainingCourse> getTrainingCourseById(@PathVariable Long idCourse) {
        return trainingCourseService.getTrainingCourseById(idCourse);
    }


    @PostMapping("assignTrainer/{trainingCourseId}/assign-trainer/{trainerId}")
    public ResponseEntity<String> assignTrainer(
            @PathVariable Long trainingCourseId,
            @PathVariable Long trainerId) {

        trainingCourseService.assignTrainer(trainingCourseId, trainerId);

        return ResponseEntity.ok().build();
    }


    @PostMapping("LIKE/{id}/like")
    public ResponseEntity<TrainingCourse> addLikeToCourse(@PathVariable("id") Long courseId) {
        TrainingCourse course = trainingCourseService.addLikeToCourse(courseId);
        return ResponseEntity.ok(course);
    }

    @PostMapping("DISLIKE/{id}/dislike")
    public ResponseEntity<TrainingCourse> addDislikeToCourse(@PathVariable("id") Long courseId) {
        TrainingCourse course = trainingCourseService.addDislikeToCourse(courseId);
        return ResponseEntity.ok(course);
    }

    @GetMapping("getRankedCourses/courses/ranked")
    public List<TrainingCourse> getRankedCourses() {
        return trainingCourseService.findAllOrderByAverageRatingDesc();
    }

    @GetMapping("openMeetingLink")
    public void openMeetingLink() {
        trainingCourseService.openMeetingLink();
    }

    @GetMapping(value = "/entity/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody StreamingResponseBody getQRCodeForEntity(@PathVariable Long id, HttpServletResponse response) throws Exception {
        int width = 300;
        int height = 300;

        TrainingCourse entity =trainingCourseService.getTrainingCourseById1(id) ; // get entity by id from database or other data source

                BitMatrix bitMatrix = trainingCourseService.generateQRCode(entity, width, height);

        response.setHeader("Content-Disposition", "attachment; filename=\"qrcode.png\"");
        response.setContentType(MediaType.IMAGE_PNG_VALUE);

        return outputStream -> {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        };
    }

}
