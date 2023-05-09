package tn.esprit.spring.Certificate;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.AppUser.AppUser;
import tn.esprit.spring.AppUser.AppUserRepository;
import tn.esprit.spring.Exception.ResourceNotFoundException;
import tn.esprit.spring.Register.RegisterRepository;
import tn.esprit.spring.TrainingCourse.TrainingCourse;
import tn.esprit.spring.TrainingCourse.TrainingCourseRepository;


import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CertificateService implements ICertificateService {

    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private TrainingCourseRepository trainingCourseRepository;
    @Autowired
    private RegisterRepository registerRepository;
    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public Certificate addCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    @Override
    public Certificate updateCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    @Override
    public boolean deleteCertificate(Long idCertificate) {
        certificateRepository.deleteById(idCertificate);
        return false;
    }

    @Override
    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    @Override
    public Certificate getCertificateById(Long idCertificate) {
        Optional<Certificate> certificate = certificateRepository.findById(idCertificate);
        return certificate.orElse(null);
    }

    @Override
    public void assignCertificateToAppUser(Long certificateId, Long appUserId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        AppUser appUser = appUserRepository.findById(appUserId)
                .orElseThrow(() -> new ResourceNotFoundException("AppUser not found"));

        certificate.setAppUser(appUser);
        certificateRepository.save(certificate);
    }

    @Override
    public void assignCertificateToTrainingCourse(Long certificateId, Long courseId) {
        // Find the certificate and training course entities
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        TrainingCourse trainingCourse = trainingCourseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Training course not found"));

        // Associate the certificate with the training course
        certificate.setTrainingCourse(trainingCourse);
        certificateRepository.save(certificate);
    }


    @Override
    public void exportCertificate(Long idCertificate, String filePath) throws IOException {
        try {
            Optional<Certificate> certif = certificateRepository.findById(idCertificate);

            String htmlContent =" <!DOCTYPE html>\n"+
                    "<!DOCTYPE html> \n" +
                    "<html>\n" +
                    "    <head><style>body{background-color: rgb(185, 161, 73);}h1{text-align: center ;font-weight: bold;color: rgb(255, 255, 255 );font-family: cursive;}p{text-align: center;font-size: medium;} </style></head>\n" +
                    "    <body>\n" +
                    "      <h1>Congrats</h1> \n \n" +
                    "      \n" +
                    "      <p>StudentName: " + certif.get().getStudentName() + "</p>\n" +
                    "      <p>Description " + certif.get().getDescription() + "</p>\n" +
                
                    "      <p>ExpirationDate: " + certif.get().getExpirationDate() + "</p>\n" +
                    //"      <p>NomUser: "+ certif.getUserRentalcontract().getLastname()+" </p>\n" +
                    //"      <p>Title Of Plan: "+  rentalContract.getRentalofferRentalContract().getTitle() +"</p>\n" +
                    //"      <p>Current Date: "+new Date() +"</p>\n" +
                    "   \n" +
                    "    </body>\n" +
                    "</html>";


                    Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("output.pdf"));

            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            XMLWorkerHelper worker = XMLWorkerHelper.getInstance();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(htmlContent.getBytes());
            worker.parseXHtml(writer, document, inputStream);

            document.close();
        } catch (DocumentException e) {
            // Handle DocumentException
        }

    }
}


