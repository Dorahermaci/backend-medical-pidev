package tn.esprit.spring.Career;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CareerService implements ICareerService {
    @Autowired
    private CareerRepository careerRepository;

    @Override
    public Career addCareer(Career career) {
        return careerRepository.save(career);
    }

    @Override
    public Career updateCareer(Career career) {
        return careerRepository.save(career);
    }

    @Override
    public void deleteCareer(Long idCareer) {
        careerRepository.deleteById(idCareer);
    }

    @Override
    public List<Career> getAllCareers() {
        return (List<Career>) careerRepository.findAll();
    }

    @Override
    public Optional<Career> getCareerById(Long idCareer) {
        return careerRepository.findById(idCareer);
    }

    @Override
    public List<Career> getCareersByAppUserId(Long idAppUser) {
        return careerRepository.findByAppUserId(idAppUser);
    }


    @Override
    public List<Career> retrieveCareersByTrainerId(Long trainerId) {
        return careerRepository.findByTrainerId(trainerId);
    }

}