package com.bhoomidarpan.dto;
import com.bhoomidarpan.entity.enums.LandType;
import lombok.Data;

@Data
public class PropertyDTO {
    private String propertyCode;
    private String location;
    private String surveyNumber;
    private String gatNumber;
    private LandType landType;
    private Double area;
    private String ownerAadhaar;

}



