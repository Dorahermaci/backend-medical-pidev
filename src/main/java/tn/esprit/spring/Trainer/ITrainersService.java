package tn.esprit.spring.Trainer;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ITrainersService {


    Trainers addTrainer(String name, String lastName, Integer phoneNumber, String email, String speciality, MultipartFile image) throws IOException;


    Trainers updateTrainers(Trainers trainers);
    void deleteTrainers(Long idTrainers);
    List<Trainers> getAllTrainers();
    Optional<Trainers> getTrainersById(Long idTrainers);

    Trainers getTrainerById(long id);

    void saveProfileImage(Trainers trainer, MultipartFile profileImage) throws IOException;

    void saveImage(MultipartFile image, String imageName) throws IOException;






}
