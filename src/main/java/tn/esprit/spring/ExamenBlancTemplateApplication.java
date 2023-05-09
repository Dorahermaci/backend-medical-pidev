package tn.esprit.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tn.esprit.spring.Material.PDFGenerator;

@SpringBootApplication
@EnableScheduling
public class ExamenBlancTemplateApplication {

	public static void main(String[] args) {


		ApplicationContext sp =SpringApplication.run(ExamenBlancTemplateApplication.class, args);

		PDFGenerator pDFGenerator = sp.getBean("pdfGenerator", PDFGenerator.class);

		pDFGenerator.generatePdfReport();
	}

}
