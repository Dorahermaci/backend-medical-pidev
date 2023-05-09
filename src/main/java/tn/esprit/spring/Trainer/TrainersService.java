package tn.esprit.spring.Trainer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class TrainersService implements ITrainersService {

    @Autowired
    private TrainersRepository trainersRepository;


    //  public void addTrainer(Trainers trainer) {
    //  trainersRepository.save(trainer);
    // }


    @Override
    public Trainers addTrainer(String name, String lastName, Integer phoneNumber, String email, String speciality, MultipartFile image) throws IOException {
        Trainers trainer = new Trainers();
        trainer.setName(name);
        trainer.setLastname(lastName);
        trainer.setPhoneNumber(phoneNumber);
        trainer.setEmail(email);
        trainer.setSpeciality(speciality);

        String imageName = name + "_" + UUID.randomUUID().toString() + ".jpg";
        trainer.setImage(imageName);
        trainersRepository.save(trainer);

        saveImage(image, imageName);

        return trainer;
    }



    @Override
    public Trainers updateTrainers(Trainers trainers) {
        return trainersRepository.save(trainers);
    }

    @Override
    public void deleteTrainers(Long idTrainers) {
        trainersRepository.deleteById(idTrainers);
    }

    @Override
    public List<Trainers> getAllTrainers() {
        return (List<Trainers>) trainersRepository.findAll();
    }

    @Override
    public Optional<Trainers> getTrainersById(Long idTrainers) {
        return trainersRepository.findById(idTrainers);
    }

    @Override
    public Trainers getTrainerById(long id) {
        return null;
    }

    @Override
    public void saveProfileImage(Trainers trainer, MultipartFile profileImage) throws IOException {

    }

    @Override
    public void saveImage(MultipartFile image, String imageName) throws IOException {
        Path imagePath = Paths.get(System.getProperty("user.home"), "images", imageName);
        Files.createDirectories(imagePath.getParent());
        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, imagePath, StandardCopyOption.REPLACE_EXISTING);
        }

}
}