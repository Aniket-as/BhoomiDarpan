package com.bhoomidarpan.controller;

import com.bhoomidarpan.entity.Property;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.service.BlockchainService;
import com.bhoomidarpan.service.CertificateService;
import com.bhoomidarpan.service.PropertyService;
import com.bhoomidarpan.service.QRCodeService;
import com.bhoomidarpan.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CertificateController {

    private final PropertyService propertyService;
    private final BlockchainService blockchainService;
    private final CertificateService certificateService;
    private final QRCodeService qrCodeService;
    private final UserService userService;

    // ================= VERIFY =================
    @GetMapping(value = "/verify/{propertyCode}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> verifyCertificate(@PathVariable String propertyCode) {

        var data = blockchainService.getPropertyFromBlockchain(propertyCode);

        String statusText = (data.getStatus() == 1) ? "ACTIVE" : "DISPUTED";
        String statusColor = (data.getStatus() == 1) ? "#16a34a" : "#dc2626";

        // 🔥 GET OWNER FROM PROPERTY → OWNERSHIP → USER
        String ownerName = propertyService.getOwnerNameByPropertyCode(propertyCode);

        String html = "<html>" +
                "<head>" +
                "<title>Property Verification</title>" +
                "<style>" +
                "body{font-family:Arial;background:#f3f4f6;text-align:center;padding:40px;}" +
                ".card{background:white;padding:30px;border-radius:12px;box-shadow:0 4px 10px rgba(0,0,0,0.1);max-width:500px;margin:auto;}" +
                ".title{font-size:22px;font-weight:bold;margin-bottom:20px;}" +
                ".status{font-size:18px;font-weight:bold;color:" + statusColor + ";}" +
                ".field{margin:10px 0;font-size:15px;}" +
                "</style>" +
                "</head>" +

                "<body>" +
                "<div class='card'>" +
                "<div class='title'>🏛 BhoomiDarpan Blockchain Verification</div>" +

                "<div class='status'>✔ VERIFIED (" + statusText + ")</div>" +

                "<div class='field'><b>Property Code:</b> " + propertyCode + "</div>" +
                "<div class='field'><b>Owner Name:</b> " + ownerName + "</div>" +  // ✅ FIXED
                "<div class='field'><b>Wallet:</b> " + data.getOwnerWallet() + "</div>" +
                "<div class='field'><b>Document Hash:</b> " + data.getDocumentHash() + "</div>" +
                "<div class='field'><b>Registered At:</b> " + data.getRegisteredAt() + "</div>" +

                "<hr/>" +
                "<small>🔐 Data fetched live from blockchain (tamper-proof)</small>" +
                "</div>" +
                "</body>" +
                "</html>";

        return ResponseEntity.ok(html);
    }

    // ================= QR DOWNLOAD =================
    @GetMapping("/qr/{propertyCode}")
    public ResponseEntity<byte[]> downloadQRCode(@PathVariable String propertyCode) {

        String verifyUrl =
                "http://localhost:8080/api/certificates/verify/" + propertyCode;

        byte[] qr = qrCodeService.generateQRCode(verifyUrl);

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=qr.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(qr);
    }

    // ================= CERTIFICATE DOWNLOAD =================
    @GetMapping("/{propertyCode}/download")
    public ResponseEntity<byte[]> downloadCertificate(
            @PathVariable String propertyCode,
            Authentication authentication   // 🔥 GET LOGGED USER
    ) {

        if (authentication == null) {
            throw new RuntimeException("Unauthorized");
        }

        // 🔥 Aadhaar-based user extraction
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found with Aadhaar"));

        // 🔥 Generate certificate with user
        byte[] pdf =
                certificateService.generateCertificate(propertyCode, user);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=" + propertyCode + "-certificate.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}