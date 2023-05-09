package tn.esprit.spring.TrainingCourse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.AppUser.AppUserRepository;
import tn.esprit.spring.Rating.RatingRepository;
import tn.esprit.spring.Register.RegisterRepository;
import tn.esprit.spring.Trainer.Trainers;
import tn.esprit.spring.Trainer.TrainersRepository;


import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class TrainingCourseService implements ITrainingCourseService {
    @Autowired
    private TrainingCourseRepository trainingCourseRepository;
    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private TrainersRepository trainersRepository;
    @Autowired
    private RatingRepository ratingRepository;


    @Override
    public TrainingCourse addTrainingCourse(TrainingCourse trainingCourse) {
        return trainingCourseRepository.save(trainingCourse);
    }

    @Override
    public TrainingCourse updateTrainingCourse(TrainingCourse trainingCourse) {
        return trainingCourseRepository.save(trainingCourse);
    }

    @Override
    public void deleteTrainingCourse(Long idCourse) {
        trainingCourseRepository.deleteById(idCourse);
    }

    @Override
    public List<TrainingCourse> getAllTrainingCourses() {
        return (List<TrainingCourse>) trainingCourseRepository.findAll();
    }

    @Override
    public Optional<TrainingCourse> getTrainingCourseById(Long idCourse) {
        return trainingCourseRepository.findById(idCourse);
    }
    @Override
    public TrainingCourse getTrainingCourseById1(Long id) {
        TrainingCourse trainingCourse = trainingCourseRepository.findById(id).get();
        return trainingCourse;

    }




    @Override
    public void assignTrainer(Long trainingCourseId, Long trainerId) {
        TrainingCourse trainingCourse = trainingCourseRepository.findById(trainingCourseId)
                .orElseThrow(() -> new RuntimeException("Training course not found with id " + trainingCourseId));

        Trainers trainer = trainersRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("Trainer not found with id " + trainerId));

        // Add trainer to the set of associated trainers for the training course
        trainingCourse.getTrainers().add(trainer);

        trainingCourseRepository.save(trainingCourse);
    }

    @Override
    public TrainingCourse addLikeToCourse(Long courseId) {
        TrainingCourse course = trainingCourseRepository.findById(courseId).orElseThrow(EntityNotFoundException::new);
        course.setLikes(course.getLikes() + 1);
        return trainingCourseRepository.save(course);
    }

    @Override
    public TrainingCourse addDislikeToCourse(Long courseId) {
        TrainingCourse course = trainingCourseRepository.findById(courseId).orElseThrow(EntityNotFoundException::new);
        course.setDislikes(course.getDislikes() + 1);
        return trainingCourseRepository.save(course);
    }

    @Override
    public List<TrainingCourse> findAllOrderByAverageRatingDesc() {
        return trainingCourseRepository.findAllOrderByAvgRatingDesc();
    }

    @Override
    public String generateMeetingLink() {

        return "https://meet.google.com/new";
        // return "{"meetUrl":"" + meetUrl + ""}";
    }

    @Override
    public void openMeetingLink() {
        try {
            String meetLink = generateMeetingLink();
            String os = System.getProperty("os.name").toLowerCase();
            Runtime rt = Runtime.getRuntime();

            if (os.contains("win")) {
                rt.exec("rundll32 url.dll,FileProtocolHandler " + meetLink);
            } else if (os.contains("mac")) {
                rt.exec("open " + meetLink);
            } else if (os.contains("nix") || os.contains("nux")) {
                rt.exec("xdg-open " + meetLink);
            }
        } catch (Exception e) {
            // Handle the exception here, e.g. log it or show an error message to the user
            e.printStackTrace();
        }
    }

    @Override
    public BitMatrix generateQRCode(TrainingCourse entity, int width, int height) throws Exception {
        String text =  "\n Name: " + entity.getCourseName() + "\n Description: " + entity.getDescription() +"\n Likes: " + entity.getLikes();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        return bitMatrix;
    }

}