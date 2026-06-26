package com.bhoomidarpan.service;

import com.bhoomidarpan.entity.Certificate;
import com.bhoomidarpan.entity.Property;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.repository.CertificateRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final PropertyService propertyService;
    private final QRCodeService qrCodeService;
    private final CertificateRepository certificateRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public byte[] generateCertificate(String propertyCode, User user) {

        Property property = propertyService.getPropertyByCode(propertyCode);

        String certNumber = "CERT-" + System.currentTimeMillis();

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            // ================= BACKGROUND =================
           PDImageXObject bg =
    PDImageXObject.createFromByteArray(
        document,
        loadImage("background.png"),
        "background"
    );
            content.drawImage(bg, 0, 0, 600, 800);

            // ================= BORDER =================
            content.setLineWidth(2);
            content.addRect(40, 40, 520, 720);
            content.stroke();

            // ================= ASHOKA LOGO =================
            PDImageXObject logo =
    PDImageXObject.createFromByteArray(
        document,
        loadImage("ashoka.png"),
        "ashoka"
    );
            content.drawImage(logo, 250, 700, 80, 80);

            // ================= TITLE =================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 18);
            content.newLineAtOffset(120, 660);
            content.showText("PROPERTY OWNERSHIP CERTIFICATE");
            content.endText();

            // ================= SUBTITLE =================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
            content.newLineAtOffset(170, 630);
            content.showText("Government of India Land Records Authority");
            content.endText();

            // ================= OWNER =================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.newLineAtOffset(150, 590);
            content.showText(user.getName().toUpperCase());
            content.endText();

            // ================= PROPERTY BOX =================
            content.setLineWidth(1.5f);
            content.addRect(100, 420, 400, 140);
            content.stroke();

            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            content.newLineAtOffset(120, 540);

            content.showText("Property Code: " + property.getPropertyCode());
            content.newLineAtOffset(0, -20);
            content.showText("Location: " + property.getLocation());
            content.newLineAtOffset(0, -20);
            content.showText("Survey No: " + property.getSurveyNumber());
            content.newLineAtOffset(0, -20);
            content.showText("Land Type: " + property.getLandType());
            content.newLineAtOffset(0, -20);
            content.showText("Area: " + property.getArea() + " sq.ft");

            content.endText();

            // ================= QR CODE =================
            String verifyUrl = baseUrl + "/certificates/verify/" + property.getPropertyCode();

            byte[] qrImage = qrCodeService.generateQRCode(verifyUrl);
            PDImageXObject qr =
                    PDImageXObject.createFromByteArray(document, qrImage, "QR");

            content.drawImage(qr, 420, 220, 100, 100);

            // ================= BARCODE =================
            byte[] barcode = generateBarcode(property.getPropertyCode());
            PDImageXObject barcodeImg =
                    PDImageXObject.createFromByteArray(document, barcode, "BARCODE");

            content.drawImage(barcodeImg, 150, 300, 250, 60);

            // ================= SIGNATURE =================
            PDImageXObject sign =
    PDImageXObject.createFromByteArray(
        document,
        loadImage("signature.png"),
        "signature"
    );
            content.drawImage(sign, 250, 200, 120, 50);

            // ================= SEAL =================
            PDImageXObject seal =
    PDImageXObject.createFromByteArray(
        document,
        loadImage("seal.png"),
        "seal"
    );
            content.drawImage(seal, 100, 180, 90, 90);

            // ================= CERTIFICATE INFO =================
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(150, 120);

            content.showText("Certificate No: " + certNumber);
            content.newLineAtOffset(0, -15);
            content.showText("Issued On: " + LocalDateTime.now());

            content.endText();

            content.close();

            // ================= SAVE PDF =================
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);

            // ================= SAVE CERTIFICATE =================
            Certificate cert = new Certificate();
            cert.setCertificateNumber(certNumber);
            cert.setIssuedAt(LocalDateTime.now());
            cert.setProperty(property);
            cert.setUser(user);
            cert.setQrCodeData(verifyUrl);

            certificateRepository.save(cert);

            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Certificate generation failed", e);
        }
    }

    private byte[] loadImage(String path) {
        try {
            return getClass()
                    .getClassLoader()
                    .getResourceAsStream(path)
                    .readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Image load failed: " + path);
        }
    }

    // ================= BARCODE =================
    private byte[] generateBarcode(String text) {
        try {
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(text, BarcodeFormat.CODE_128, 300, 80);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", baos);

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Barcode generation failed", e);
        }
    }
}
