package tn.esprit.spring.Certificate;


import java.io.IOException;
import java.util.List;

public interface ICertificateService {



    Certificate addCertificate(Certificate certificate);

    Certificate updateCertificate(Certificate certificate);

    boolean deleteCertificate(Long idCertificate);

    List<Certificate> getAllCertificates();

    Certificate getCertificateById(Long idCertificate);


    void assignCertificateToAppUser(Long certificateId, Long appUserId);

    void assignCertificateToTrainingCourse(Long certificateId, Long courseId);

    //byte[] generatePdfFromHtml(String htmlFilePath, String pdfFilePath) throws IOException, DocumentException, java.io.IOException;

   // ByteArrayOutputStream generateCertificate(String name) throws IOException, com.itextpdf.text.DocumentException, DocumentException;

    void exportCertificate(Long idCertificate, String filePath) throws IOException;
}
