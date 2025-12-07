package com.example.voiture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryCl

ient;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceVoitureApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceVoitureApplication.class, args);
    }
}
