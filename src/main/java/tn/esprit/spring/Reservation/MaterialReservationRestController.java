package tn.esprit.spring.Reservation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/materialReservation")

public class MaterialReservationRestController {
    @Autowired
    private MaterialReservationRepository materialReservationRepository;

    @Autowired
    IMaterialReservationService materialReservationService;

    @GetMapping("/retrieve-All-MaterialReservations")
    public List<MaterialReservation> getMaterialReservations() {
        List<MaterialReservation> listMaterialReservations = materialReservationService.retrieveAllMaterialReservations();
        return listMaterialReservations;
    }

    @PostMapping("/add-materialReservation")
    public ResponseEntity<MaterialReservation> addMaterialReservation(@RequestBody MaterialReservation materialReservation) {
        try {
            MaterialReservation savedMaterialReservation = materialReservationService.addMaterialReservation(materialReservation);
            return new ResponseEntity<>(savedMaterialReservation, HttpStatus.CREATED);
        } catch (MessagingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (javax.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    @PutMapping("/update-materialReservation")
    public MaterialReservation updateMaterialReservation(@RequestBody MaterialReservation materialReservation) {

        return materialReservationService.updateMaterialReservation(materialReservation);

    }



    @DeleteMapping("/remove-MaterialReservation/{idReservation}")
    public void removeMaterialReservation(@PathVariable("idReservation") Integer idReservation) {

        materialReservationService.removeMaterialReservation(idReservation);
    }
}
