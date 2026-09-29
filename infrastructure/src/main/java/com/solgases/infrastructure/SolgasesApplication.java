package com.solgases.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.solgases")
public class SolgasesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SolgasesApplication.class, args);
    }
}
