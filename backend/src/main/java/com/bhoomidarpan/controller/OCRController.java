package com.bhoomidarpan.controller;

import com.bhoomidarpan.dto.OCRRequest;
import com.bhoomidarpan.dto.OCRResponse;
import com.bhoomidarpan.service.OCRService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ocr")
@RequiredArgsConstructor
public class OCRController {

    private final OCRService ocrService;

    @PostMapping("/extract")
    public ResponseEntity<OCRResponse> extractText(@Valid @ModelAttribute OCRRequest request) {
        OCRResponse response = ocrService.extractTextFromDocument(request.getDocument(), request.getDocumentType());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-court-doc")
    public ResponseEntity<Boolean> validateCourtDocument(@Valid @ModelAttribute OCRRequest request) {
        OCRResponse response = ocrService.extractTextFromDocument(request.getDocument(), "COURT_ORDER");
        boolean isValid = ocrService.validateCourtDocument(response.getExtractedText());
        return ResponseEntity.ok(isValid);
    }
}