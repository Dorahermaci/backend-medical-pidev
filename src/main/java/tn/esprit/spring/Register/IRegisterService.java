package tn.esprit.spring.Register;

import tn.esprit.spring.AppUser.AppUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IRegisterService {



    Register addRegister(LocalDateTime StartDate);

    Register updateRegister(Register register);

    void deleteRegister(Long idRegister);

    List<Register> getAllRegisters();

    Optional<Register> getRegisterById(Long idRegister);

    List<Register> getRegistersByTrainingCourseId(Long courseId);

    List<Register> getRegistersByAppUserId(Long idAppUser);




    List<Register> getRegistersByCompletionStatus(boolean completionStatus);

   // void joinCourse(Long courseId, Long appUserId, Register register);


  //  void joinCourse(Long courseId, Long appUserId, LocalDateTime StartDate);

    void joinCourse(Long courseId, Long appUserId);

    long countRegisteredUsersForCourse(long courseId);

    int countSucceededUsersInCourse(long courseId);

    int countFailedUsersInCourse(long courseId);

    int calculateScoresForUser(Long idAppUser);

    // Method to get a list of AppUsers sorted by their score
    List<AppUser> getRankedAppUsers();


}
