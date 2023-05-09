package tn.esprit.spring.MedicalFolder;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicalFileServiceImpl implements MedicalFileService {

    @Autowired
    private MedicalFileRepository medicalFileRepository;

    @Override
    public MedicalFile saveOrUpdateMedicalFile(MedicalFile medicalFile) {
        return medicalFileRepository.save(medicalFile);
    }

    @Override
    public void deleteMedicalFile(Long id) {
        medicalFileRepository.deleteById(id);
    }

    @Override
    public MedicalFile getMedicalFileById(Long id) {
        return medicalFileRepository.findById(id).orElse(null);
    }

    @Override
    public List<MedicalFile> getAllMedicalFiles() {
        return medicalFileRepository.findAll();
    }

    @Override
    public List<MedicalFile> getAllMedicalFilesByMedicalFolder(MedicalFolder mf) {
        return medicalFileRepository.findByMedicalFolder(mf);

    }


}
