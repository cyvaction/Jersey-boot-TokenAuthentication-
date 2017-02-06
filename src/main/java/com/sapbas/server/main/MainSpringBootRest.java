package com.sapbas.server.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import com.sapbas.server.javaconfig.RestConfig;

@SpringBootApplication
@ComponentScan({ "com.sapbas.server" })
@Import({ RestConfig.class })
public class MainSpringBootRest {
    
    public static void main(String[] args) {
        SpringApplication.run(MainSpringBootRest.class, args);
    }
}