package tn.esprit.spring.MedicalUser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/Staff")

public class MedicalStaffRestController {
    @Autowired
    private MedicalStaffRepository medicalStaffRepository;

    @Autowired
    IMedicalStaffService medicalStaffService;

    @GetMapping("/retrieve-All-MedicalStaffs")
    public List<MedicalStaff> getMedicalStaffs() {
        List<MedicalStaff> listMedicalStaffs = medicalStaffService.retrieveAllMedicalStaffs();
        return listMedicalStaffs;
    }

    @PostMapping("/add-medicalStaff")
    public MedicalStaff addMedicalStaff(@RequestBody MedicalStaff medicalStaff) {
        medicalStaffService.addMedicalStaff(medicalStaff);
        return medicalStaff;
    }


    @PutMapping("/update-medicalStaff")
    public MedicalStaff updateMedicalStaff(@RequestBody MedicalStaff medicalStaff) {

        return medicalStaffService.updateMedicalStaff(medicalStaff);

    }



    @DeleteMapping("/remove-MedicalStaff/{idStaff}")
    public void removeMedicalStaff(@PathVariable("idStaff") Integer idStaff) {

        medicalStaffService.removeMedicalStaff(idStaff);
    }


}
