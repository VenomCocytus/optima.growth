package com.optimagrowth.license;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.optimagrowth.commonlibrary")
public class LicenseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                LicenseServiceApplication.class, args);
    }

}
