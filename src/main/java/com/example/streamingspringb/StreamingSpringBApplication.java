package com.example.streamingspringb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

@SpringBootApplication
public class StreamingSpringBApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamingSpringBApplication.class, args);
    }

}
