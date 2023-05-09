package tn.esprit.spring.Register;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserRepository;
import tn.esprit.spring.Exception.ResourceNotFoundException;
import tn.esprit.spring.TrainingCourse.TrainingCourse;
import tn.esprit.spring.TrainingCourse.TrainingCourseRepository;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RegisterService implements IRegisterService {
    @Autowired
    private RegisterRepository registerRepository;

    @Autowired
    private TrainingCourseRepository trainingCourseRepository;

    @Autowired
    private AppUserRepository appUserRepository;


    @Override
    public Register addRegister(LocalDateTime StartDate) {
        Register register = new Register();
        register.setStartDate(StartDate);
        return registerRepository.save(register);
    }


    @Override
    public Register updateRegister(Register register) {
        return registerRepository.save(register);
    }

    @Override
    public void deleteRegister(Long idRegister) {
        registerRepository.deleteById(idRegister);
    }

    @Override
    public List<Register> getAllRegisters() {
        return (List<Register>) registerRepository.findAll();
    }

    @Override
    public Optional<Register> getRegisterById(Long idRegister) {
        return registerRepository.findById(idRegister);
    }


    @Override
    public List<Register> getRegistersByTrainingCourseId(Long idCourse) {
        return registerRepository.findByTrainingCourseId(idCourse);
    }

    @Override
    public List<Register> getRegistersByAppUserId(Long idAppUser) {
        return registerRepository.findByAppUserId(idAppUser);
    }

    @Override
    public List<Register> getRegistersByCompletionStatus(boolean completionStatus) {
        return registerRepository.findByCompletionStatus(completionStatus);
    }

    @Override
    public void joinCourse(Long courseId, Long appUserId) {
        Register register = new Register();
        register.setStartDate(LocalDateTime.now());
        register.setCompletionStatus(false);
        TrainingCourse course = trainingCourseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        AppUser appUser = appUserRepository.findById(appUserId)
                .orElseThrow(() -> new ResourceNotFoundException("AppUser not found"));
        register.setTrainingCourse(course);
        register.setAppUser(appUser);
        registerRepository.save(register);
    }

    @Override
    public long countRegisteredUsersForCourse(long courseId) {
        return registerRepository.countByTrainingCourseId(courseId);
    }

    @Override
    public int countSucceededUsersInCourse(long courseId) {
        return registerRepository.countSucceededUsersInCourse(courseId);
    }

    @Override
    public int countFailedUsersInCourse(long courseId) {
        return registerRepository.countFailedUsersInCourse(courseId);

    }

    @Override
    public int calculateScoresForUser(Long idAppUser) {
        List<Register> registers = registerRepository.findByAppUserIdAndCompletionStatusTrue(idAppUser);
        int totalScore = 0;
        for (Register register : registers) {
            TrainingCourse trainingCourse = register.getTrainingCourse();
            LocalDateTime startDate = register.getStartDate();
            LocalDateTime endDate = register.getEndDate();
            long timeTaken = ChronoUnit.SECONDS.between(startDate, endDate);
            long estimatedTime = trainingCourse.getEstimatedTime() * 3600;
            long timeDifference = timeTaken - estimatedTime;

            int score = 0;
            if (timeDifference < 0) {
                score = 10;
            } else if (timeDifference == 0) {
                score = 5;
            } else {
                score = -5; // or some other negative score
            }

            totalScore += score;
        }

        AppUser userToUpdate = appUserRepository.findById(idAppUser).orElse(null);
        if (userToUpdate != null) {
            int currentScore = userToUpdate.getScore();
            userToUpdate.setScore(currentScore + totalScore);
            appUserRepository.save(userToUpdate);
        }

        return totalScore;
    }

    // Method to get a list of AppUsers sorted by their score
    @Override
    public List<AppUser> getRankedAppUsers() {
        List<AppUser> appUsers = appUserRepository.findAll();
        appUsers.sort(Comparator.comparingInt(AppUser::getScore).reversed());
        return appUsers;
    }



}