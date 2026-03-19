package com.supermarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SupermarketApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupermarketApiApplication.class, args);
    }

}