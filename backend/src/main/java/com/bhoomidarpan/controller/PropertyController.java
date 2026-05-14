package com.bhoomidarpan.controller;

import com.bhoomidarpan.dto.*;
import com.bhoomidarpan.entity.Property;
import com.bhoomidarpan.entity.User;
import com.bhoomidarpan.exception.BhoomiDarpanException;
import com.bhoomidarpan.service.PropertyService;
import com.bhoomidarpan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "http://localhost:*", allowCredentials = "true")
public class PropertyController {

    private final PropertyService propertyService;
    private final UserService userService;

    // ==============================
    // 🔥 ADMIN CREATE PROPERTY
    // ==============================
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUB_REGISTRAR')")
    @PostMapping(value = "/admin-create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPropertyWithDocuments(
            @RequestPart("property") PropertyDTO propertyDTO,
            @RequestPart("sevenTwelve") MultipartFile sevenTwelveFile,
            @RequestPart("saleDeed") MultipartFile saleDeedFile,
            @RequestPart(value = "otherDocuments", required = false) List<MultipartFile> otherDocuments,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new BhoomiDarpanException("Unauthorized access");
        }

        User admin = userService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        Property property = propertyService.createPropertyWithDocuments(
                propertyDTO,
                sevenTwelveFile,
                saleDeedFile,
                otherDocuments,
                admin
        );

        return ResponseEntity.ok(
                "Property created successfully. Code: " + property.getPropertyCode()
        );
    }

    // ==============================
    // 🔥 GET PROPERTY DETAILS
    // ==============================
    @GetMapping("/{propertyCode}")
    public ResponseEntity<PropertyDetailResponse> getPropertyDetails(
            @PathVariable String propertyCode,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new BhoomiDarpanException("Unauthorized access");
        }

        Property property = propertyService.getPropertyByCode(propertyCode);

        PropertyDetailResponse response = convertToDetailResponse(property);

        return ResponseEntity.ok(response);
    }

    // ==============================
    // 🔥 GET PROPERTY DOCUMENTS
    // ==============================
    @GetMapping("/{propertyCode}/documents")
    public ResponseEntity<?> getPropertyDocuments(
            @PathVariable String propertyCode,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new BhoomiDarpanException("Unauthorized access");
        }

        Property property = propertyService.getPropertyByCode(propertyCode);

        return ResponseEntity.ok(
                propertyService.getDocumentsByProperty(property.getId())
        );
    }

    // ==============================
    // 🔥 VERIFIED PROPERTIES
    // ==============================
    @GetMapping("/verified")
    public ResponseEntity<List<PropertyResponse>> getVerifiedPropertiesForBuy(
            @RequestParam(required = false) String area) {

        List<Property> properties =
                propertyService.getVerifiedPropertiesForBuy(area);

        List<PropertyResponse> response = properties.stream()
                .map(this::convertToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // ==============================
    // 🔥 AVAILABLE PROPERTIES
    // ==============================
    @GetMapping("/available")
    public ResponseEntity<List<PropertyResponse>> getAvailableProperties(
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new BhoomiDarpanException("Unauthorized access");
        }

        User currentUser = userService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        List<Property> properties =
                propertyService.getAvailablePropertiesExcludingOwner(
                        search,
                        currentUser.getId()
                );

        List<PropertyResponse> response = properties.stream()
                .map(this::convertToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // ==============================
    // 🔥 MY PROPERTIES
    // ==============================
    @GetMapping("/my-properties")
    public ResponseEntity<List<PropertyResponse>> getMyProperties(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new BhoomiDarpanException("Unauthorized access");
        }

        User currentUser = userService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        List<Property> properties =
                propertyService.getPropertiesByOwner(currentUser.getId());

        List<PropertyResponse> response = properties.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ==============================
    // 🔥 CHECK AVAILABILITY
    // ==============================
    @GetMapping("/{propertyId}/available")
    public ResponseEntity<Boolean> checkPropertyAvailability(
            @PathVariable Long propertyId) {

        boolean isAvailable =
                propertyService.isPropertyAvailableForSale(propertyId);

        return ResponseEntity.ok(isAvailable);
    }

    // ==============================
    // 🔥 DASHBOARD STATS
    // ==============================
    @GetMapping("/dashboard-stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new BhoomiDarpanException("Unauthorized access");
        }

        User currentUser = userService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        DashboardStatsResponse stats = new DashboardStatsResponse();

        stats.setProperties(
                propertyService.countPropertiesByOwner(currentUser.getId())
        );

        stats.setTransactions(
                propertyService.countActiveTransactions(currentUser.getId())
        );

        stats.setMutationPending(
                propertyService.countPendingMutations(currentUser.getId())
        );

        stats.setCertificates(
                propertyService.countCertificates(currentUser.getId())
        );

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{propertyCode}/owner")
    public ResponseEntity<?> getOwnerDetails(
            @PathVariable String propertyCode,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new BhoomiDarpanException("Unauthorized");
        }

        String ownerName = propertyService.getOwnerNameByPropertyCode(propertyCode);

        return ResponseEntity.ok(ownerName);
    }

    @PutMapping("/{propertyCode}/toggle-sale")
    public ResponseEntity<Boolean> toggleSale(
            @PathVariable String propertyCode,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new BhoomiDarpanException("Unauthorized access");
        }

        User currentUser = userService
                .findByAadhaarNumber(userDetails.getUsername())
                .orElseThrow(() -> new BhoomiDarpanException("User not found"));

        boolean status = propertyService
                .toggleSaleStatus(propertyCode, currentUser.getId());

        return ResponseEntity.ok(status);
    }

    // ==============================
    // 🔥 PRIVATE CONVERTERS
    // ==============================
    private PropertyResponse convertToResponse(Property property) {
        PropertyResponse response = new PropertyResponse();
        response.setId(property.getId());
        response.setPropertyCode(property.getPropertyCode());
        response.setLocation(property.getLocation());
        response.setStatus(property.getStatus().name());
        response.setLandType(property.getLandType().name());
        response.setArea(property.getArea());
        response.setCreatedAt(property.getCreatedAt().toString());
        return response;
    }

    private PropertyDetailResponse convertToDetailResponse(Property property) {

        PropertyDetailResponse response = new PropertyDetailResponse();

        response.setId(property.getId());
        response.setPropertyCode(property.getPropertyCode());
        response.setLocation(property.getLocation());
        response.setSurveyNumber(property.getSurveyNumber());
        response.setGatNumber(property.getGatNumber());
        response.setLandType(property.getLandType().name());
        response.setStatus(property.getStatus().name());
        response.setArea(property.getArea());
        response.setCreatedAt(property.getCreatedAt().toString());
        response.setAvailableForSale(property.isAvailableForSale());

        // Owners
        var owners = propertyService.getCurrentOwners(property.getId());
        response.setOwners(
                owners.stream().map(o -> {
                    PropertyDetailResponse.OwnerInfo owner =
                            new PropertyDetailResponse.OwnerInfo();
                    owner.setUserId(o.getUser().getId());
                    owner.setName(o.getUser().getName());
                    owner.setOwnershipPercentage(o.getOwnershipPercentage());
                    owner.setOwnershipType(o.getOwnershipType().name());
                    return owner;
                }).toList()
        );

        // Documents
        var documents = propertyService.getDocumentsByProperty(property.getId());
        response.setDocuments(
                documents.stream().map(doc -> {
                    PropertyDetailResponse.DocumentInfo info =
                            new PropertyDetailResponse.DocumentInfo();
                    info.setId(doc.getId());
                    info.setDocumentType(doc.getDocumentType());
                    info.setFileUrl(doc.getFileUrl());
                    info.setVerified(doc.isVerified());
                    return info;
                }).toList()
        );

        return response;
    }
}
