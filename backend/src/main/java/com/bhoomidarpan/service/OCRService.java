package com.bhoomidarpan.service;

import com.bhoomidarpan.dto.OCRResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class OCRService {

    @Value("${app.ocr.tessdata-path}")
    private String tessdataPath;

    @Value("${app.ocr.languages}")
    private String languages;

    /* ========================= MAIN ENTRY ========================= */

    public OCRResponse extractTextFromDocument(MultipartFile file, String documentType) {

        OCRResponse response = new OCRResponse();

        try {

            String extractedText;

            if (isPDF(file)) {
                extractedText = extractFromPDF(file);
            } else {
                extractedText = runOCR(readImage(file));
            }

            Map<String, String> extractedFields =
                    processExtractedText(extractedText, documentType);

            response.setSuccess(true);
            response.setExtractedText(extractedText);
            response.setExtractedFields(extractedFields);
            response.setValidationStatus("VALID");

        } catch (Exception e) {
            log.error("OCR processing failed", e);
            response.setSuccess(false);
            response.setValidationStatus("INVALID");
        }

        return response;
    }

    /* ========================= PDF HANDLING ========================= */

    private boolean isPDF(MultipartFile file) {
        return file.getContentType() != null &&
                file.getContentType().equalsIgnoreCase("application/pdf");
    }

    /**
     * Hybrid PDF extraction:
     * 1. Try direct text extraction
     * 2. If empty → fallback to OCR
     */
    private String extractFromPDF(MultipartFile file) throws IOException, TesseractException {

        try (PDDocument document = PDDocument.load(file.getBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text != null && text.trim().length() > 30) {
                log.info("Using direct PDF text extraction (Digital PDF detected)");
                return text;
            }

            log.info("Digital text not found. Falling back to OCR.");

            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, 600); // High DPI
            return runOCR(image);
        }
    }

    /* ========================= OCR ========================= */

    private Tesseract createTesseractInstance() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessdataPath);
        tesseract.setLanguage(languages);
        tesseract.setPageSegMode(3);
        tesseract.setOcrEngineMode(3);
        tesseract.setTessVariable("user_defined_dpi", "600");
        return tesseract;
    }

    private String runOCR(BufferedImage image) throws TesseractException {
        if (image == null) throw new IllegalArgumentException("Invalid image file");

        Tesseract tesseract = createTesseractInstance();
        return tesseract.doOCR(image);
    }

    private BufferedImage readImage(MultipartFile file) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(file.getBytes()));
    }

    /* ========================= FIELD EXTRACTION ========================= */

    private Map<String, String> processExtractedText(String text, String documentType) {

        System.out.println("========== FULL EXTRACTED TEXT ==========");
        System.out.println(text);
        System.out.println("=========================================");

        Map<String, String> fields = new HashMap<>();

        switch (documentType) {
            case "SALE_DEED":
                extractSaleDeedFields(text, fields);
                break;
            case "COURT_ORDER":
                extractCourtOrderFields(text, fields);
                break;
            case "INHERITANCE_CERTIFICATE":
                extractInheritanceFields(text, fields);
                break;
            case "SEVEN_TWELVE":
                extractSevenTwelveFields(text, fields);
                break;
            case "EIGHT_A":
                extractEightAFields(text, fields);
                break;

            default:
                extractCommonFields(text, fields);
        }

        return fields;
    }

    private void extractSaleDeedFields(String text, Map<String, String> fields) {

        // Flexible seller pattern
        Pattern sellerPattern = Pattern.compile(
                "(?is)seller.*?name\\s*[:\\-]?\\s*([A-Za-z ]{3,})"
        );

        Matcher sellerMatcher = sellerPattern.matcher(text);

        if (sellerMatcher.find()) {
            String sellerName = sellerMatcher.group(1).trim();
            fields.put("sellerName", sellerName);
            log.info("Seller detected: {}", sellerName);
        }


        // Flexible buyer pattern
        Pattern buyerPattern = Pattern.compile(
                "(?is)buyer.*?name\\s*[:\\-]?\\s*([A-Za-z ]{3,})"
        );

        Matcher buyerMatcher = buyerPattern.matcher(text);

        if (buyerMatcher.find()) {
            String buyerName = buyerMatcher.group(1).trim();
            fields.put("buyerName", buyerName);
            log.info("Buyer detected: {}", buyerName);
        }
    }

    private void extractCourtOrderFields(String text, Map<String, String> fields) {

        Pattern courtPattern =
                Pattern.compile("(?i)(?:in\\s*the\\s*)?([A-Za-z\\s]+Court)");
        Matcher courtMatcher = courtPattern.matcher(text);
        if (courtMatcher.find()) {
            fields.put("courtName", courtMatcher.group(1).trim());
        }

        Pattern casePattern =
                Pattern.compile("(?i)case\\s*no[.:\\s]*([A-Za-z0-9\\-/]+)");
        Matcher caseMatcher = casePattern.matcher(text);
        if (caseMatcher.find()) {
            fields.put("caseNumber", caseMatcher.group(1).trim());
        }
    }

    private void extractInheritanceFields(String text, Map<String, String> fields) {
        Pattern deceasedPattern =
                Pattern.compile("(?i)(?:deceased|late)[\\s:]*([A-Za-z\\s]+)");
        Matcher matcher = deceasedPattern.matcher(text);
        if (matcher.find()) {
            fields.put("deceasedName", matcher.group(1).trim());
        }
    }

    private void extractCommonFields(String text, Map<String, String> fields) {

        Pattern datePattern =
                Pattern.compile("\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}");
        Matcher dateMatcher = datePattern.matcher(text);
        if (dateMatcher.find()) {
            fields.put("documentDate", dateMatcher.group());
        }
    }

    /* ========================= SMART VALIDATION ========================= */

    public boolean validateSaleDeed(
            String extractedText, String expectedBuyer, String expectedSeller) {

        String text = extractedText.toLowerCase();

        boolean buyerMatch = text.contains(expectedBuyer.toLowerCase());
        boolean sellerMatch = text.contains(expectedSeller.toLowerCase());

        return buyerMatch && sellerMatch;
    }

    public boolean validateCourtDocument(String extractedText) {

        if (extractedText == null) return false;

        String text = extractedText.toLowerCase();

        if (!text.contains("court")) {
            return false;
        }

        Pattern casePattern =
                Pattern.compile("(?i)case\\s*no[.:\\s]*[A-Za-z0-9\\-/]+");

        return casePattern.matcher(extractedText).find();
    }

    private void extractSevenTwelveFields(String text, Map<String, String> fields) {

        Pattern ownerPattern = Pattern.compile(
                "(?i)(owner|holder|khatedar)\\s*name?\\s*[:\\-]?\\s*([A-Za-z ]{3,})"
        );

        Matcher matcher = ownerPattern.matcher(text);

        if (matcher.find()) {
            fields.put("ownerName", matcher.group(2).trim());
        }
    }

    private void extractEightAFields(String text, Map<String, String> fields) {

        Pattern ownerPattern = Pattern.compile(
                "(?i)(owner|account\\s*holder)\\s*name?\\s*[:\\-]?\\s*([A-Za-z ]{3,})"
        );

        Matcher matcher = ownerPattern.matcher(text);

        if (matcher.find()) {
            fields.put("ownerName", matcher.group(2).trim());
        }
    }


    public boolean validateMutationDocs(
            String sevenTwelveText,
            String eightAText,
            String expectedOwner) {

        if (sevenTwelveText == null || eightAText == null) return false;

        String owner = expectedOwner.toLowerCase();

        return sevenTwelveText.toLowerCase().contains(owner)
                && eightAText.toLowerCase().contains(owner);
    }

}