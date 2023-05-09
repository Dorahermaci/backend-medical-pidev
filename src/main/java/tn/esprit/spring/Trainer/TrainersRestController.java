package tn.esprit.spring.Trainer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/Trainers")
public class TrainersRestController {
    @Autowired
    private ITrainersService trainersService;

    @PostMapping(value = "/addTrainer", consumes = "multipart/form-data")
    public ResponseEntity<Trainers> addTrainer(
            @RequestParam String name,
            @RequestParam String lastName,
            @RequestParam Integer phoneNumber,
            @RequestParam  String email,
            @RequestParam String speciality,
             @RequestParam MultipartFile image) throws IOException {
        Trainers trainer = trainersService.addTrainer(name, lastName, phoneNumber, email, speciality, image);
        return ResponseEntity.ok(trainer);
    }

    @PutMapping("updateTrainers/{id}")
    public ResponseEntity<Trainers> updateTrainers(@PathVariable("id") Long idTrainers, @RequestBody Trainers trainers) {
        Optional<Trainers> currentTrainers = trainersService.getTrainersById(idTrainers);
        if (currentTrainers.isPresent()) {
            Trainers updatedTrainers = trainersService.updateTrainers(trainers);
            return new ResponseEntity<>(updatedTrainers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("deleteTrainers/{id}")
    public ResponseEntity<HttpStatus> deleteTrainers(@PathVariable("id") Long idTrainers) {
        Optional<Trainers> currentTrainers = trainersService.getTrainersById(idTrainers);
        if (currentTrainers.isPresent()) {
            trainersService.deleteTrainers(idTrainers);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("getAllTrainers/")
    public ResponseEntity<List<Trainers>> getAllTrainers() {
        List<Trainers> trainersList = trainersService.getAllTrainers();
        if (trainersList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(trainersList, HttpStatus.OK);
        }
    }

    @GetMapping("getTrainersById/{id}")
    public ResponseEntity<Trainers> getTrainersById(@PathVariable("id") Long idTrainers) {
        Optional<Trainers> currentTrainers = trainersService.getTrainersById(idTrainers);
        if (currentTrainers.isPresent()) {
            return new ResponseEntity<>(currentTrainers.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



}
