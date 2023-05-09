package tn.esprit.spring.Reservation;


import javax.mail.MessagingException;
import java.util.List;

public interface IMaterialReservationService {

    public List<MaterialReservation> retrieveAllMaterialReservations();

    public MaterialReservation updateMaterialReservation (MaterialReservation materialReservation);

    public MaterialReservation addMaterialReservation (MaterialReservation materialReservation) throws MessagingException;

    public MaterialReservation retrieveMaterialReservation (Integer  idReservation);

    public void removeMaterialReservation(Integer idReservation);

}
