package tn.esprit.spring.Register;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.AppUser.AppUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/Register")
public class RegisterRestController {
    @Autowired
    private IRegisterService registerService;



    @PutMapping("updateRegister/{id}")
    public ResponseEntity<Register> updateRegister(@PathVariable Long id, @RequestBody Register register) {
        Optional<Register> optionalRegister = registerService.getRegisterById(id);
        if (optionalRegister.isPresent()) {
            register.setIdRegister(id);
            Register updatedRegister = registerService.updateRegister(register);
            return ResponseEntity.ok(updatedRegister);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("deleteRegister/{id}")
    public ResponseEntity<Void> deleteRegister(@PathVariable Long id) {
        Optional<Register> optionalRegister = registerService.getRegisterById(id);
        if (optionalRegister.isPresent()) {
            registerService.deleteRegister(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("getAllRegisters")
    public ResponseEntity<List<Register>> getAllRegisters() {
        List<Register> registers = registerService.getAllRegisters();
        if (registers.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(registers);
        }
    }



    @PostMapping("joinCourse/{courseId}/")
    public ResponseEntity<Object> joinCourse(@PathVariable Long courseId, @RequestParam Long appUserId ) {
       Register register = new Register();
        register.setStartDate(LocalDateTime.now());
        register.setCompletionStatus(false);
        registerService.joinCourse(courseId, appUserId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("Statistics/successRate/{courseId}")
    public ResponseEntity<String> getSuccessRateForCourse(@PathVariable Long courseId) {
        Long numSignedUp = registerService.countRegisteredUsersForCourse(courseId);
        int numsuccess = registerService.countSucceededUsersInCourse(courseId);
        int numfailed = registerService.countFailedUsersInCourse(courseId);
        double successRate = numsuccess == 0 ? 0 : ((double)  numsuccess/numSignedUp ) * 100;
        return ResponseEntity.ok("Number of people signed up for course "+ numSignedUp +"  Success for course number" + courseId + " : " + numsuccess + " number of Failed "+numfailed+" Ratio : "+successRate+" % ");
    }



        @PostMapping("/calculateScore/{appUserId}")
        public ResponseEntity<String> calculateScore( @PathVariable Long appUserId) {
            int score = registerService.calculateScoresForUser(appUserId);
            return ResponseEntity.ok( "the appuser  Score is " + score);
        }

    @GetMapping("/app-users/ranked")
    public List<AppUser> getRankedAppUsers() {
        return registerService.getRankedAppUsers();
    }

}














