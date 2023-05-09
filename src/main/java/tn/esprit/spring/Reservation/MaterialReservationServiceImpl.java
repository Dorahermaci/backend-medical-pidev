package tn.esprit.spring.Reservation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.email.EmailSenderService;

import javax.mail.MessagingException;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Service
@Slf4j
public class MaterialReservationServiceImpl implements IMaterialReservationService {

    @Autowired
    MaterialReservationRepository materialReservationRepository;
    @Autowired
     EmailSenderService emailSenderService;

    @Override
    public List<MaterialReservation> retrieveAllMaterialReservations() {
        return materialReservationRepository.findAll();
    }

    @Override
    public MaterialReservation updateMaterialReservation(MaterialReservation materialReservation) {
        return materialReservationRepository.save(materialReservation);}

    public MaterialReservation addMaterialReservation(MaterialReservation materialReservation) throws MessagingException {
        MaterialReservation savedMaterialReservation = materialReservationRepository.save(materialReservation);

        // Send email notification
        String recipientEmail = "nourhene.mezrigui@esprit.tn";
        String subject = "New Material Reservation Added";
        String message = "A new material reservation has been added with the following details:\n\n" +
                "Material Name: " + savedMaterialReservation.getMaterial() + "\n" +
                "Quantity: " + savedMaterialReservation.getQuantity() + "\n" +
                "Date: " + savedMaterialReservation.getDate() + "\n" ;
        String attachmentFilePath = "C:\\Users\\MYLAPTOP\\Desktop\\Facture.png";
        emailSenderService.SendMailWithAttachment(recipientEmail, subject, message, attachmentFilePath);

        return savedMaterialReservation;
    }


    @Override
    public MaterialReservation retrieveMaterialReservation(Integer idReservation) {
        return null;
    }

    @Override
    public void removeMaterialReservation(Integer idReservation) { materialReservationRepository.deleteById(idReservation);}
}
