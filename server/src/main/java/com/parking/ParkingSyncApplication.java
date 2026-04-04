package com.parking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ParkingSyncApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParkingSyncApplication.class, args);
    }
}
