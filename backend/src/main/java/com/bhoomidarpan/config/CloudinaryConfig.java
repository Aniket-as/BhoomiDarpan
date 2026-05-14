package com.bhoomidarpan.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(Map.of(
                "cloud_name", "dz95pyghh",
                "api_key", "221665158775233",
                "api_secret", "TDIrBNWwa-0MXk5JZZFrpFStT7o"
        ));
    }
}