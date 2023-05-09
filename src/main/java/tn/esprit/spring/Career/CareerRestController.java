package tn.esprit.spring.Career;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/Career")
public class CareerRestController {

    @Autowired
    private ICareerService careerService;

    @PostMapping("/addCareer")
    public ResponseEntity<Career> addCareer(@RequestBody Career career) {
        Career addedCareer = careerService.addCareer(career);
        return ResponseEntity.ok(addedCareer);
    }

    @PutMapping("updateCareer/{idCareer}")
    public ResponseEntity<Career> updateCareer(@PathVariable Long idCareer, @RequestBody Career career) {
        Optional<Career> existingCareer = careerService.getCareerById(idCareer);
        if (existingCareer.isPresent()) {
            career.setIdCareer(idCareer);
            Career updatedCareer = careerService.updateCareer(career);
            return ResponseEntity.ok(updatedCareer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("deleteCareer/{idCareer}")
    public ResponseEntity<Void> deleteCareer(@PathVariable Long idCareer) {
        Optional<Career> existingCareer = careerService.getCareerById(idCareer);
        if (existingCareer.isPresent()) {
            careerService.deleteCareer(idCareer);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("getAllCareer")
    public ResponseEntity<List<Career>> getAllCareers() {
        List<Career> careers = careerService.getAllCareers();
        if (careers.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(careers);
        }
    }

    @GetMapping("getCareerById/{idCareer}")
    public ResponseEntity<Career> getCareerById(@PathVariable Long idCareer) {
        Optional<Career> career = careerService.getCareerById(idCareer);
        if (career.isPresent()) {
            return ResponseEntity.ok(career.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("getCareersByAppUserId/{idCareer}")
    public ResponseEntity<List<Career>> getCareersByAppUserId(@PathVariable Long idCareer) {
        List<Career> careers = careerService.getCareersByAppUserId(idCareer);
        if (careers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(careers, HttpStatus.OK);
    }
    @GetMapping("/careersByTrainerId/{trainerId}")
    public List<Career> getCareersByTrainerId(@PathVariable("trainerId") Long trainerId) {
        return careerService.retrieveCareersByTrainerId(trainerId);
    }
}

