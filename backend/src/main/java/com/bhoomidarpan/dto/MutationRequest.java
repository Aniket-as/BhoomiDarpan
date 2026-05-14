package com.bhoomidarpan.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MutationRequest {

    private String remarks;
    private MultipartFile sevenTwelve;
    private MultipartFile eightA;
}



