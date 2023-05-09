package tn.esprit.spring.Career;

import java.util.List;
import java.util.Optional;

public interface ICareerService {


    Career addCareer(Career career);

    Career updateCareer(Career career);

    void deleteCareer(Long idCareer);

    List<Career> getAllCareers();

    Optional<Career> getCareerById(Long idCareer);

    List<Career> getCareersByAppUserId(Long idAppUser);

  //  List<Career> getCareersByTrainerId(Long idTrainer);



    List<Career> retrieveCareersByTrainerId(Long trainerId);
}
