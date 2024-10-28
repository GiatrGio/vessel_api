package com.example.vessel_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class VesselApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VesselApiApplication.class, args);
    }

}
