package com.viseo.apph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class ApphBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApphBackApplication.class, args);
    }
}
