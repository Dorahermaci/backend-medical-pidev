package tn.esprit.spring.Supplier;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/supplier")
public class SupplierRestController {

    @Autowired
    ISupplierService supplierService;
    @Autowired
    SupplierRepository supplierRepository;

    @GetMapping("/retrieve-All-Suppliers")
    public List<Supplier> getSuppliers() {
        List<Supplier> listSuppliers = supplierService.retrieveAllSuppliers();
        return listSuppliers;
    }

    @PostMapping("/add-supplier")
    public Supplier addSupplier(@RequestBody Supplier supplier) {
        supplierService.addSupplier(supplier);
        return supplier;
    }


    @PutMapping("/update-supplier")
    public Supplier updateSupplier(@RequestBody Supplier supplier) {

        return supplierService.updateSupplier(supplier);

    }



    @DeleteMapping("/remove-Supplier/{idSupplier}")
    public void removeSupplier(@PathVariable("idSupplier") Integer idSupplier) {

        supplierService.removeSupplier(idSupplier);
    }


    @GetMapping(value = "/suppliers/{idSupplier}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode(@PathVariable("idSupplier") Integer idSupplier) throws WriterException, IOException {
        Supplier supplier = supplierRepository.findById(idSupplier).orElse(null);
        if (supplier == null) {
            return ResponseEntity.notFound().build();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(generateQRCodeContent(supplier), BarcodeFormat.QR_CODE, 350, 350);
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", stream);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<byte[]>(stream.toByteArray(), headers, HttpStatus.OK);
    }

    private String generateQRCodeContent(Supplier supplier) {
        StringBuilder builder = new StringBuilder();
        builder.append("Name: ").append(supplier.getSupplierName()).append("\n");
        builder.append("Adress: ").append(supplier.getAdress()).append("\n");
        builder.append("Phone: ").append(supplier.getPhoneNumber()).append("\n");
        return builder.toString();
    }



}
