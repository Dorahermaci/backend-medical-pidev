package tn.esprit.spring.Supplier;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j

public class SupplierServieImpl implements ISupplierService {


    @Autowired
    SupplierRepository supplierRepository;

    @Override
    public List<Supplier> retrieveAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier updateSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }



    @Override
    public Supplier addSupplier(Supplier supplier) {
        String qrContent = supplier.getIdSupplier() + "_" + UUID.randomUUID().toString();
        supplier.setQrContent(qrContent);

        // Générer l'image du QR code
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] qrCodeImage = outputStream.toByteArray();
        supplier.setQrCodeImage(qrCodeImage);
        return supplierRepository.save(supplier);


    }


    @Override
    public Supplier retrieveSupplier(Integer idSupplier) {
        return null;
    }

    @Override
    public void removeSupplier(Integer idSupplier) { supplierRepository.deleteById(idSupplier);}


}
